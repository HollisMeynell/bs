package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.entity.osu.OsuUser;
import l.f.mappool.entity.pool.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.LogException;
import l.f.mappool.exception.NotFoundException;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.pool.PoolMark4UserRepository;
import l.f.mappool.repository.pool.PoolUserRepository;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MapPoolService {
    @Resource
    MapPoolDao mapPoolDao;
    @Resource
    PoolUserRepository poolUserRepository;
    @Resource
    UserService userService;

    @Resource
    PoolMark4UserRepository poolMark4UserRepository;

    /***
     * 用户创建的图池数量到了最大值
     * @param user 用户
     * @return 当超过数量限制, 即严格大于时返回 true
     */
    public boolean isMax(OsuUser user) {
        int count = poolUserRepository.getUserCreatedSize(user.getOsuId());
        return count > user.getMaxPoolSize();
    }

    public Pool createMapPool(long userId, String name, String banner, String info) {
        var user = userService.getOsuUser(userId);
        if (this.isMax(user)) {
            throw new RuntimeException("to many");
        }
        return mapPoolDao.createPool(user.getOsuId(), name, banner, info);
    }

    public Pool updateMapPool(long userId, int poolId, String name, String banner, String info) {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }

        var poolOpt = mapPoolDao.getMapPoolById(poolId);
        if (poolOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var pool = poolOpt.get();
        if (name != null && !name.isBlank()) {
            pool.setName(name);
        }
        if (banner != null && !banner.isBlank()) {
            pool.setBanner(banner);
        }
        if (info != null && !info.isBlank()) {
            pool.setInfo(info);
        }

        return mapPoolDao.saveMapPool(pool);
    }

    public void deleteMapPool(long userId, int poolId) throws HttpError {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var poolOpt = mapPoolDao.getMapPoolById(poolId);
        if (poolOpt.isEmpty()) {
            throw new HttpError(403, "已被删除");
        }
        var pool = poolOpt.get();
        if (!pool.getGroups().isEmpty()) {
            throw new HttpError(403, "图池不为空,请删掉全部内容.");
        }
        if (pool.getUsers().size() > 1) {
            throw new HttpError(403, "图池仍有成员,请删掉所有其他成员.");
        }
        mapPoolDao.deletePool(userId, pool);
    }

    public void removePool(long userId, int poolId) throws HttpError {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var poolOpt = mapPoolDao.getMapPoolById(poolId);
        if (poolOpt.isEmpty()) {
            throw new HttpError(403, "已被删除");
        }
        var pool = poolOpt.get();
        if (pool.getStatus() != PoolStatus.DELETE) {
            throw new HttpError(403, "先执行delete");
        }
        mapPoolDao.removePool(userId, pool);
    }

    public void exportPool(long userId, int poolId) throws HttpError {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }

        var poolOptional = mapPoolDao.getMapPoolById(poolId);
        if (poolOptional.isEmpty()) {
            throw new NotFoundException();
        }

        var pool = poolOptional.get();
        if (!pool.getStatus().equals(PoolStatus.OPEN)) {
            throw new HttpError(403, "无法导出非编辑中的图池");
        }

        mapPoolDao.exportPool(pool);
    }

    /***
     * 创建一个分类组,比如 NM,HD 这种
     */
    public PoolCategoryGroup createCategoryGroup(long uid, int poolId, String name, String info, int color) {
        if (!mapPoolDao.isAdminByPool(poolId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategoryGroup(uid, poolId, name, info, color);
    }

    public PoolCategoryGroup updateCategoryGroup(long uid, int groupId, String name, String info, Integer color, Integer sort) {
        if (!mapPoolDao.isAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }

        var groupOpt = mapPoolDao.getCategoryGroupById(groupId);
        if (groupOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var group = groupOpt.get();
        if (name != null && !name.isBlank()) {
            group.setName(name);
        }
        if (info != null && !info.isBlank()) {
            group.setInfo(info);
        }
        if (color != null) {
            group.setColor(color);
        }
        if (sort != null) {
            group.setSort(sort);
        }

        return mapPoolDao.saveMapCategoryGroup(group);
    }

    public void deleteCategoryGroup(long uid, int groupId) {
        if (!mapPoolDao.isAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }

        var groupOpt = mapPoolDao.getCategoryGroupById(groupId);
        if (groupOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var group = groupOpt.get();
        if (group.getCategories().size() > 0) {
            throw new RuntimeException("类别不为空,请删掉全部内容.");
        }

        mapPoolDao.deleteMapCategoryGroup(group);
    }

    /***
     * 具体分类 比如 NM1,NM2 这种
     */
    public PoolCategory createCategory(long uid, int groupId, String name) {
        if (!mapPoolDao.isAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategory(uid, groupId, name);
    }

    public PoolCategory updateCategory(long uid, int categoryId, String name) {
        var categoryOpt = mapPoolDao.getMapCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var category = categoryOpt.get();
        if (!mapPoolDao.isAdminByGroup(category.getGroupId(), uid)) {
            throw new PermissionException();
        }

        if (name != null && !name.isBlank()) {
            category.setName(name);
        }

        return mapPoolDao.saveCategory(category);
    }

    public void deleteCategory(long uid, int categoryId) {
        var categoryOpt = mapPoolDao.getMapCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var category = categoryOpt.get();
        if (!mapPoolDao.isAdminByGroup(category.getGroupId(), uid)) {
            throw new PermissionException();
        }
        mapPoolDao.deleteCategory(category);
    }

    public PoolCategory choseCategory(long uid, int categoryId, Long bid) {
        var categoryOpt = mapPoolDao.getMapCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var category = categoryOpt.get();
        if (!mapPoolDao.isAdminByGroup(category.getGroupId(), uid)) {
            throw new PermissionException();
        }

        category.setChosed(bid);

        return mapPoolDao.saveCategory(category);
    }

    public PoolCategoryItem createCategoryItem(long uid, int categoryId, long bid, String info) {
        if (!mapPoolDao.isChooserByCategory(categoryId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategoryItem(uid, categoryId, bid, info);
    }

    public PoolCategoryItem updateCategoryItem(long uid, int itemId, long bid, String info, int sort) {
        var item = mapPoolDao.checkItem(uid, itemId);
        return mapPoolDao.updateCategoryItem(item, bid, info, sort);
    }

    public List<PoolFeedback> getFeedbackFromItem(long uid, int itemId) {
        var itemOpt = mapPoolDao.getMapCategoryItemById(itemId);
        return itemOpt
                .map(PoolCategoryItem::getFeedbacks)
                .map(e -> {
                    e.removeIf(i -> (i.getCreaterId() != uid && i.isHandle()));
                    return e;
                })
                .orElseGet(List::of);
    }

    public List<PoolFeedback> getPublicFeedbackFromItem(int itemId) {
        var itemOpt = mapPoolDao.getMapCategoryItemById(itemId);
        return itemOpt
                .map(PoolCategoryItem::getFeedbacks)
                .map(e -> {
                    e.removeIf(PoolFeedback::isHandle);
                    return e;
                })
                .orElseGet(List::of);
    }


    public void deleteCategoryItem(long uid, int itemId) {
        var item = mapPoolDao.checkItem(uid, itemId);
        mapPoolDao.deleteCategoryItem(item);
    }

    public Map<PoolPermission, List<Pool>> getAllPool(long osuId) {
        return mapPoolDao.getAllPool(osuId);
    }

    public List<PoolCategoryGroup> getCategoryGroup(int poolId) {
        return mapPoolDao.getAllCategotys(poolId);
    }

    public List<Pool> queryByNameAndId(QueryMapPoolDto query, long userId) {

        if (query.getPoolId() != null) {
            var data = mapPoolDao.queryById(query.getPoolId());
            return data.map(List::of).orElseGet(() -> new ArrayList<>(0));
        } else {
            // 查询的页码从0开始
            return mapPoolDao.queryByName(query.getPoolName(), userId, query.getPageNum() - 1, query.getPageSize());
        }
    }

    public int countByNameAndId(QueryMapPoolDto query, long userId) {
        if (query.getPoolId() != null) {
            return mapPoolDao.queryCountById(query.getPoolId());
        }
        return mapPoolDao.countByName(query.getPoolName(), userId);
    }

    public void addMarkPool(long uid, int pid) {
        var search = poolMark4UserRepository.queryMapPoolMark4UserByUidAndPid(uid, pid);
        if (search.isPresent()) return;
        var f = new PoolMark4User();
        f.setUid(uid);
        f.setPid(pid);
        poolMark4UserRepository.saveAndFlush(f);
    }

    public int deleteMarkPool(long uid, int pid) {
        return poolMark4UserRepository.deleteAllByUidaAndPid(uid, pid);
    }

    public DataListVo<Pool> getAllMarkPool(long uid) {
        var list = mapPoolDao.getAllMarkPool(uid);
        return new DataListVo<Pool>()
                .setTotalItems(list.size())
                .setPageSize(list.size())
                .setData(list);
    }

    public DataVo<PoolUser> addAdminUser(long userId, long addUserId, int poolId) {
        if (!mapPoolDao.isCreaterByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addAdminUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public DataVo<PoolUser> addChooserUser(long userId, long addUserId, int poolId) {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addChooserUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public DataVo<PoolUser> addTesterUser(long userId, long addUserId, int poolId) {
        if (!mapPoolDao.isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addTesterUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public void deleteUser(long userId, long deleteUserId, int poolId) {
        if (!mapPoolDao.isCreaterByPool(poolId, userId)) {
            throw new PermissionException();
        }
        if (userId == deleteUserId) {
            throw new LogException("不能删除自己", 401);
        }
        mapPoolDao.deleteUser(userId, deleteUserId, poolId);
    }
}
