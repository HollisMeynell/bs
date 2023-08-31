package l.f.mappool.dao;

import jakarta.persistence.EntityManager;
import l.f.mappool.entity.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MapPoolDao {
    private final MapPoolRepository          poolRepository;
    private final MapFeedbackRepository      feedbackRepository;
    private final MapPoolUserRepository      poolUserRepository;
    private final MapCategoryRepository      categoryRepository;
    private final MapCategoryItemRepository  categoryItemRepository;
    private final MapCategoryGroupRepository categoryGroupRepository;

    private final EntityManager entityManager;

    @Autowired
    public MapPoolDao(MapPoolRepository mapPoolRepository,
                      MapPoolUserRepository mapPoolUserRepository,
                      MapCategoryRepository mapCategoryRepository,
                      MapCategoryGroupRepository mapCategoryGroupRepository,
                      MapCategoryItemRepository mapCategoryItemRepository,
                      MapFeedbackRepository mapFeedbackRepository,
                      EntityManager entityManager
    ) {
        poolRepository = mapPoolRepository;
        poolUserRepository = mapPoolUserRepository;
        categoryRepository = mapCategoryRepository;
        categoryGroupRepository = mapCategoryGroupRepository;
        categoryItemRepository = mapCategoryItemRepository;
        feedbackRepository = mapFeedbackRepository;
        this.entityManager = entityManager;
    }

    /************************************************** Pool *****************************************************************/

    /***
     * 创建
     * @return 新 MapPool
     */
    public MapPool createPool(long userId, String poolName, String banner, String info) {
        var map = new MapPool();
        map.setName(poolName);
        map.setInfo(info);
        map.setBanner(banner);
        if (poolRepository.hasPool(poolName) == 0) {
            map = poolRepository.save(map);
            addUser(userId, map.getId(), PoolPermission.CREATE);
            // 写入权限
            return map;
        } else {
            throw new RuntimeException("创建失败: 图池已经存在");
        }
    }

    public List<MapPool> queryByName(String name, long userId, int page, int size) {
        return poolRepository.queryByName(name, userId, PageRequest.of(page, size));
    }

    public int countByName(String name, long userId) {
        return poolRepository.countByName(name, userId);
    }

    public Optional<MapPool> queryById(int id) {
        return poolRepository.getByIdNotDelete(id);
    }

    public List<MapPool> getPublicPool() {
        return poolRepository.getAllOpenPool();
    }

    /***
     * 获取用户下所有可见的图池
     * @param userId uid
     * @return 权限+图池
     */
    public Map<PoolPermission, List<MapPool>> getAllPool(long userId) {
        var map = new HashMap<PoolPermission, List<MapPool>>();
        var ulist = poolUserRepository.searchAllByUserId(userId);

        var uCreates = ulist.stream().filter(u -> u.getPermission() == PoolPermission.CREATE).map(MapPoolUser::getPool).toList();
        var uAdmins = ulist.stream().filter(u -> u.getPermission() == PoolPermission.ADMIN).map(MapPoolUser::getPool).toList();
        var uChoosers = ulist.stream().filter(u -> u.getPermission() == PoolPermission.CHOOSER).map(MapPoolUser::getPool).toList();
        var uTesters = ulist.stream().filter(u -> u.getPermission() == PoolPermission.TESTER).map(MapPoolUser::getPool).toList();

        map.put(PoolPermission.CREATE, uCreates);
        map.put(PoolPermission.ADMIN, uAdmins);
        map.put(PoolPermission.CHOOSER, uChoosers);
        map.put(PoolPermission.TESTER, uTesters);

        return map;
    }

    public List<MapPool> getAllPublicPool() {
        return poolRepository.getAllOpenPool();
    }

    public List<MapPool> getAllPublicPoolExcludeUser(long uid) {
        return poolRepository.getAllOpenPool();
    }

    public List<MapPool> getAllMarkPool(long uid) {
        return poolRepository.queryByUserMark(uid);
    }

    public Optional<MapPool> getMapPoolById(int id) {
        return poolRepository.getByIdNotDelete(id);
    }

    public MapPool saveMapPool(MapPool pool) {
        return poolRepository.saveAndFlush(pool);
    }

    /***
     * 真删除
     * @param uid uid
     * @param pid pool id
     */
    public void removePool(long uid, int pid) {
        if (!isAdminByPool(pid, uid)) {
            throw new PermissionException();
        }
        var poolOpt = poolRepository.getById(pid);
        if (poolOpt.isEmpty()) {
            throw new RuntimeException("已被删除");
        }
        var pool = poolOpt.get();
        if (pool.getStatus() != PoolStatus.DELETE) {
            throw new RuntimeException("先执行delete");
        }
        poolUserRepository.deleteByPool(pool);
        poolRepository.delete(pool);
    }

    public void deletePool(long uid, int pid) {
        if (!isAdminByPool(pid, uid)) {
            throw new PermissionException();
        }
        var poolOpt = poolRepository.getByIdNotDelete(pid);
        if (poolOpt.isEmpty()) {
            throw new RuntimeException("已被删除");
        }
        var pool = poolOpt.get();
        if (!pool.getGroups().isEmpty()) {
            throw new RuntimeException("图池不为空,请删掉全部内容.");
        }
        if (!pool.getUsers().isEmpty()) {
            throw new RuntimeException("图池仍有成员,请删掉所有其他成员.");
        }
        pool.setStatus(PoolStatus.DELETE);
        poolRepository.save(pool);
    }

    /************************************************** User *****************************************************************/

    public int queryCountById(int id) {
        return poolRepository.getCountById(id);
    }

    public MapPoolUser addAdminUser(long userId, long addUserId, int poolId) {
        if (!isCreaterByPool(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.ADMIN);
    }


    public MapPoolUser addChooserUser(long userId, long addUserId, int poolId) {
        if (!isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.CHOOSER);
    }

    public MapPoolUser addTesterUser(long userId, long addUserId, int poolId) {
        if (!isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.TESTER);
    }

    private boolean testBef(Optional<MapPoolUser> userOpt, PoolPermission... poolPermissions) {
        if (userOpt.isEmpty()) {
            return false;
        }
        if (poolPermissions.length == PoolPermission.values().length) {
            return true;
        }
        var user = userOpt.get().getPermission();
        for (var p : poolPermissions) {
            if (p.equals(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询权限 pool
     *
     * @param poolPermissions 包含
     * @return
     */
    public boolean testPermissionByPool(int poolId, long userId, PoolPermission... poolPermissions) {
        var userOpt = poolUserRepository.getMapPoolUserByPoolIdAndUserId(poolId, userId);
        return testBef(userOpt, poolPermissions);
    }


    public boolean isCreaterByPool(int poolId, long userId) {
        return testPermissionByPool(poolId, userId, PoolPermission.CREATE);
    }

    public boolean isAdminByPool(int poolId, long userId) {
        return testPermissionByPool(poolId, userId, PoolPermission.CREATE, PoolPermission.ADMIN);
    }

    public boolean isChooserByPool(int poolId, long userId) {
        return testPermissionByPool(poolId, userId, PoolPermission.CREATE, PoolPermission.ADMIN, PoolPermission.CHOOSER);
    }

    public boolean isUserByPool(int poolId, long userId) {
        return testPermissionByPool(poolId, userId, PoolPermission.values());
    }

    public boolean testPermissionByCategoryGroup(int groupId, long userId, PoolPermission... poolPermissions) {
        var userOpt = poolUserRepository.getMapPoolUserByGroupIdAndUserId(groupId, userId);
        return testBef(userOpt, poolPermissions);
    }

    public boolean isCreaterByGroup(int groupId, long userId) {
        return testPermissionByCategoryGroup(groupId, userId, PoolPermission.CREATE);
    }

    public boolean isAdminByGroup(int groupId, long userId) {
        return testPermissionByCategoryGroup(groupId, userId, PoolPermission.CREATE, PoolPermission.ADMIN);
    }

    public boolean isChooserByGroup(int groupId, long userId) {
        return testPermissionByCategoryGroup(groupId, userId, PoolPermission.CREATE, PoolPermission.ADMIN, PoolPermission.CHOOSER);
    }

    public boolean isChooserByCategory(int categoryId, long userId) {
        var userOpt = poolUserRepository.getMapPoolUserByCategoryIdAndUserId(categoryId, userId);
        return testBef(userOpt, PoolPermission.CREATE, PoolPermission.ADMIN, PoolPermission.CHOOSER);
    }

    public boolean isUserByGroup(int groupId, long userId) {
        return testPermissionByCategoryGroup(groupId, userId, PoolPermission.values());
    }

    public MapPoolUser addUser(long addUserId, int poolId, PoolPermission permission) {
        var u = poolUserRepository.getMapPoolUserByPoolIdAndUserId(poolId, addUserId);
        MapPoolUser addUser;
        if (u.isPresent()) {
            addUser = u.get();
            addUser.setPermission(permission);
        } else {
            addUser = new MapPoolUser();
            addUser.setUserId(addUserId);
            addUser.setPoolId(poolId);
            addUser.setPermission(permission);
        }
        return poolUserRepository.save(addUser);
    }

    /************************************************** Group *****************************************************************/

    /***
     *  创建分组 比如NM组
     * @param userId
     * @param poolId
     * @param name
     * @param info
     * @param color
     * @return
     */
    public MapCategoryGroup createCategoryGroup(long userId, int poolId, String name, String info, int color) {
        if (!isAdminByPool(poolId, userId)) {
            throw new PermissionException();
        }

        var mg = new MapCategoryGroup();
        mg.setPoolId(poolId);
        mg.setColor(color);
        mg.setName(name);
        mg.setInfo(info);
        mg.setSort(0);
        return categoryGroupRepository.save(mg);
    }

    public Optional<MapCategoryGroup> getCategoryGroupById(int groupId) {
        return categoryGroupRepository.findById(groupId);
    }

    public MapCategoryGroup saveMapCategoryGroup(MapCategoryGroup group) {
        return categoryGroupRepository.saveAndFlush(group);
    }

    /************************************************** Category *****************************************************************/

    /**
     * 创建具体分类 比如NM1
     *
     * @param groupId      CategoryGroup.id
     * @param categoryName
     * @return
     */
    public MapCategory createCategory(long userId, int groupId, String categoryName) {
        if (!isAdminByGroup(groupId, userId)) {
            throw new PermissionException();
        }

        var category = new MapCategory();
        category.setGroupId(groupId);
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    /************************************************** Item *****************************************************************/

    /***
     * 加一张图
     * @return 包含推荐人id, bid, 描述信息的结构
     */
    public MapCategoryItem createCategoryItem(long userId, int categoryId, long bid, String info) {
        var categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("not found");
        }
        var category = categoryOpt.get();
        if (!isChooserByGroup(category.getGroupId(), userId)) {
            throw new PermissionException();
        }
        var categoryItem = new MapCategoryItem();
        categoryItem.setSort(0);
        categoryItem.setInfo(info);
        categoryItem.setChous(bid);
        categoryItem.setCategoryId(categoryId);
        return categoryItemRepository.save(categoryItem);
    }

    public MapFeedback createFeedback(long userId, int categoryItemId, @Nullable Boolean agree, String msg) {
        var categoryItem = categoryItemRepository.findById(categoryItemId);
        if (categoryItem.isEmpty()) {
            throw new RuntimeException("not found");
        }
        var category = categoryItem.get().getCategory();
        if (!isUserByGroup(category.getGroupId(), userId)) {
            throw new PermissionException();
        }
        var feedback = new MapFeedback();
        feedback.setAgree(agree);
        feedback.setFeedback(msg);
        feedback.setCreaterId(userId);
        feedback.setItemId(categoryItemId);
        return feedbackRepository.save(feedback);
    }

    public List<MapCategoryGroup> getAllCategotys(int poolId) {
        var groups = categoryRepository.getAllCategory(poolId);
        for (var g : groups) {
            g.setCategories(categoryRepository.getAllByGroup(g));
        }
        return groups;
    }

    public List<MapCategoryItem> getAllCategoryItems(int categoryId) {
        var selectAttr = new MapCategory();
        selectAttr.setId(categoryId);
        return categoryItemRepository.findAllByCategory(selectAttr);
    }
}
