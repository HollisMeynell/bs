package l.f.mappool.service;

import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.entity.osu.OsuOauthUser;
import l.f.mappool.entity.pool.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.HttpTipException;
import l.f.mappool.exception.NotFoundException;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.pool.PoolMark4UserRepository;
import l.f.mappool.repository.pool.PoolUserRepository;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.PoolVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MapPoolService {

    MapPoolDao mapPoolDao;

    PoolUserRepository poolUserRepository;

    UserService userService;

    PoolMark4UserRepository poolMark4UserRepository;

    /***
     * 用户创建的图池数量到了最大值
     * @param user 用户
     * @return 当超过数量限制, 即严格大于时返回 true
     */
    public boolean isMax(OsuOauthUser user) {
        int count = poolUserRepository.getUserCreatedSize(user.getOsuId());
        return count > user.getMaxPoolSize();
    }

    public Pool createMapPool(long userId, String name, String banner, String info, int mode) {
        var user = userService.getOsuUser(userId);
        if (this.isMax(user)) {
            throw new RuntimeException("to many");
        }
        return mapPoolDao.createPool(user.getOsuId(), name, banner, info, mode);
    }

    public Pool updateMapPool(long userId, int poolId, String name, String banner, String info, int mode) {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }

        var poolOpt = mapPoolDao.getMapPoolById(poolId);
        if (poolOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var pool = poolOpt.get();
        pool.setMode(mode);
        if (StringUtils.hasText(name)) {
            pool.setName(name);
        }
        if (StringUtils.hasText(banner)) {
            pool.setBanner(banner);
        }
        if (StringUtils.hasText(info)) {
            pool.setInfo(info);
        }

        return mapPoolDao.saveMapPool(pool);
    }

    public void deleteMapPool(long userId, int poolId) throws HttpError {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
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
        poolMark4UserRepository.deleteAllByPid(poolId);
    }

    public void removePool(long userId, int poolId) throws HttpError {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
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

    public PoolVo exportPool(long userId, int poolId) throws HttpError {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
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

        return mapPoolDao.exportPool(pool);
    }

    public PoolVo getExportPool(int poolId) throws HttpError {
        return mapPoolDao.getExportPool(poolId);
    }

    /***
     * 创建一个分类组,比如 NM,HD 这种
     */
    public PoolCategoryGroup createCategoryGroup(
            long uid,
            int poolId,
            String name,
            String info,
            int color,
            Optional<Integer> modRequired,
            Optional<Integer> modOptional
    ) {
        if (mapPoolDao.notAdminByPool(poolId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategoryGroup(uid, poolId, name, info, color, modRequired, modOptional);
    }

    public PoolCategoryGroup updateCategoryGroup(
            long uid,
            int groupId,
            String name,
            String info,
            Optional<Integer> color,
            Optional<Integer> sort,
            Optional<Integer> modRequired,
            Optional<Integer> modOptional
    ) {
        if (mapPoolDao.notAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }

        var groupOpt = mapPoolDao.getCategoryGroupById(groupId);
        if (groupOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var group = groupOpt.get();
        if (StringUtils.hasText(name)) {
            group.setName(name);
        }
        if (StringUtils.hasText(info)) {
            group.setInfo(info);
        }

        color.ifPresent(group::setColor);
        sort.ifPresent(group::setSort);
        modRequired.ifPresent(group::setModsRequired);
        modOptional.ifPresent(group::setModsOptional);

        return mapPoolDao.saveMapCategoryGroup(group);
    }

    public void deleteCategoryGroup(long uid, int groupId) {
        if (mapPoolDao.notAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }

        var groupOpt = mapPoolDao.getCategoryGroupById(groupId);
        if (groupOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var group = groupOpt.get();
        if (CollectionUtils.isEmpty(group.getCategories())) {
            throw new RuntimeException("类别不为空,请删掉全部内容.");
        }

        mapPoolDao.deleteMapCategoryGroup(group);
    }

    /***
     * 具体分类 比如 NM1,NM2 这种
     */
    public PoolCategory createCategory(long uid, int groupId, String name) {
        if (mapPoolDao.notAdminByGroup(groupId, uid)) {
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
        if (mapPoolDao.notAdminByGroup(category.getGroupId(), uid)) {
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
        if (mapPoolDao.notAdminByGroup(category.getGroupId(), uid)) {
            throw new PermissionException();
        }
        mapPoolDao.deleteCategory(category);
    }

    public PoolCategory choseCategory(long uid, int categoryId, int itemId) {
        var categoryOpt = mapPoolDao.getMapCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }

        var category = categoryOpt.get();
        if (mapPoolDao.notAdminByGroup(category.getGroupId(), uid)) {
            throw new PermissionException();
        }

        var itemOpt = mapPoolDao.getMapCategoryItemById(itemId);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException();
        }
        category.setChosed(itemOpt.get().getChous());

        return mapPoolDao.saveCategory(category);
    }

    public PoolCategoryItem createCategoryItem(long uid, int categoryId, long bid, String info) {
        if (!mapPoolDao.notChooserByCategory(categoryId, uid)) {
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
        if (mapPoolDao.notCreaterByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addAdminUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public DataVo<PoolUser> addChooserUser(long userId, long addUserId, int poolId) {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addChooserUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public DataVo<PoolUser> addTesterUser(long userId, long addUserId, int poolId) {
        if (mapPoolDao.notAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        var u = mapPoolDao.addTesterUser(userId, addUserId, poolId);
        return new DataVo<>(u);
    }

    public void deleteUser(long userId, long deleteUserId, int poolId) {
        if (mapPoolDao.notCreaterByPool(poolId, userId)) {
            throw new PermissionException();
        }
        if (userId == deleteUserId) {
            throw new HttpTipException(401, "不能删除自己");
        }
        mapPoolDao.deleteUser(userId, deleteUserId, poolId);
    }

    /***********************************  ADMIN  ******************************************/
    public PoolVo exportPoolAdmin(long userId, PoolVo pool) {
        var p = mapPoolDao.createPool(userId, pool.getName(), pool.getBanner(), pool.getInfo(), pool.getMode());
        for (var categoryGroupVo : pool.getCategoryList()) {
            var group = mapPoolDao.createCategoryGroup(userId,
                    p.getId(),
                    categoryGroupVo.getName(),
                    categoryGroupVo.getInfo(),
                    categoryGroupVo.getColor(),
                    Optional.ofNullable(categoryGroupVo.getModsOptional()),
                    Optional.ofNullable(categoryGroupVo.getModsRequired())
            );

            for (var categoryVo : categoryGroupVo.getCategory()) {
                var category = new PoolCategory();
                category.setGroupId(group.getId());
                category.setName(categoryVo.name());
                category.setChosed(categoryVo.bid());
                category = mapPoolDao.saveCategory(category);
                if (categoryVo.creater() != null) {
                    mapPoolDao.createCategoryItem(categoryVo.creater(), category.getId(), categoryVo.bid(), null);
                }
            }
        }

        p.setStatus(PoolStatus.SHOW);
        mapPoolDao.saveMapPool(p);
        var newPool = mapPoolDao.getMapPoolById(p.getId()).orElseThrow(() -> new HttpTipException("出现了不可能出现的异常, 查查日志"));

        return new PoolVo(newPool);
    }

    @Transactional
    public void deletePoolAdmin(int poolId) {
        poolMark4UserRepository.deleteAllByPid(poolId);
        mapPoolDao
                .removePoolForce(mapPoolDao.getMapPoolById(poolId)
                        .orElseThrow(() -> new HttpTipException("不存在或者已删除")));
    }
}
