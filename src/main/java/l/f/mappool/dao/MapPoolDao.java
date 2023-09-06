package l.f.mappool.dao;

import jakarta.persistence.EntityManager;
import l.f.mappool.entity.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.NotFoundException;
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
@SuppressWarnings("unused")
public class MapPoolDao {
    private final MapPoolRepository poolRepository;
    private final MapFeedbackRepository feedbackRepository;
    private final MapPoolUserRepository poolUserRepository;
    private final MapCategoryRepository categoryRepository;
    private final MapCategoryItemRepository categoryItemRepository;
    private final MapCategoryGroupRepository categoryGroupRepository;

    @SuppressWarnings("all")
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

    /* ************************************************* Pool **************************************************************** */

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
        var poolOpt = poolRepository.getById(pid);
        if (poolOpt.isEmpty()) {
            throw new RuntimeException("已被删除");
        }
        var pool = poolOpt.get();
        if (pool.getStatus() != PoolStatus.DELETE) {
            throw new RuntimeException("先执行delete");
        }
        if (poolUserRepository.deleteByPool(pool) != -1) {
            poolRepository.delete(pool);
        }
    }

    public void deletePool(long uid, int pid) {
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
        return addUser(addUserId, poolId, PoolPermission.ADMIN);
    }


    public MapPoolUser addChooserUser(long userId, long addUserId, int poolId) {
        return addUser(addUserId, poolId, PoolPermission.CHOOSER);
    }

    public MapPoolUser addTesterUser(long userId, long addUserId, int poolId) {
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
     * @return yes or no
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

    /* ************************************************* Group **************************************************************** */

    /***
     *  创建分组 比如NM组
     */
    public MapCategoryGroup createCategoryGroup(long userId, int poolId, String name, String info, int color) {
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

    public List<MapCategoryGroup> getAllCategotys(int poolId) {
        var groups = categoryRepository.getAllCategory(poolId);
        return groups;
    }

    public void deleteMapCategoryGroup(MapCategoryGroup group) {
        categoryGroupRepository.delete(group);
    }

    /* ************************************************* Category **************************************************************** */

    /**
     * 创建具体分类 比如NM1
     *
     * @param groupId CategoryGroup.id
     */
    public MapCategory createCategory(long userId, int groupId, String categoryName) {
        var category = new MapCategory();
        category.setGroupId(groupId);
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    public MapCategory saveCategory(MapCategory category) {
        return categoryRepository.saveAndFlush(category);
    }

    public void deleteCategory(MapCategory category) {
        for (var item : category.getItems()) {
            deleteCategoryItem(item);
        }
        categoryRepository.delete(category);
    }

    public Optional<MapCategory> getMapCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /* ************************************************* Item **************************************************************** */

    /***
     * 加一张图
     * @return 包含推荐人id, bid, 描述信息的结构
     */
    public MapCategoryItem createCategoryItem(long userId, int itemId, long bid, String info) {
        var categoryOpt = categoryRepository.findById(itemId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }
        var category = categoryOpt.get();
        var categoryItem = new MapCategoryItem();
        categoryItem.setSort(0);
        categoryItem.setInfo(info);
        categoryItem.setChous(bid);
        categoryItem.setCreaterId(userId);
        categoryItem.setCategoryId(itemId);
        return categoryItemRepository.save(categoryItem);
    }

    public MapCategoryItem updateCategoryItem(long userId, int itemId, long bid, String info, int sort) {
        var item = checkItem(userId, itemId);

        item.setChous(bid);
        item.setSort(sort);
        item.setInfo(info);

        return categoryItemRepository.save(item);
    }

    public void deleteCategoryItem(long userId, int itemId) {
        var item = checkItem(userId, itemId);
        deleteCategoryItem(item);
    }

    private void deleteCategoryItem(MapCategoryItem item) {
        if (item.getFeedbacks().size() != 0) {
            feedbackRepository.deleteAll(item.getFeedbacks());
        }
        categoryItemRepository.delete(item);
    }

    private MapCategoryItem checkItem(long userId, int itemId) {
        var categoryItemOpt = categoryItemRepository.findById(itemId);
        if (categoryItemOpt.isEmpty()) throw new NotFoundException();
        var item = categoryItemOpt.get();

        if (!isAdminByGroup(item.getCategory().getGroupId(), userId) && !item.getCreaterId().equals(userId)) {
            throw new PermissionException("非创建者无法操作");
        }

        return item;
    }

    public Optional<MapCategoryItem> getMapCategoryItemById(int itemId) {
        return categoryItemRepository.findById(itemId);
    }

    public List<MapCategoryItem> getAllCategoryItems(int categoryId) {
        var selectAttr = new MapCategory();
        selectAttr.setId(categoryId);
        return categoryItemRepository.findAllByCategory(selectAttr);
    }

    /* ************************************************* Other **************************************************************** */

    public MapFeedback createFeedback(long userId, int categoryItemId, @Nullable Boolean agree, String msg) {
        var categoryItem = categoryItemRepository.findById(categoryItemId);
        if (categoryItem.isEmpty()) {
            throw new NotFoundException();
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

    public MapFeedback updateFeedback(long userId, int FeedbackId, @Nullable Boolean agree, String msg) {
        var feedback = checkFeedback(userId, FeedbackId);
        feedback.setAgree(agree);
        feedback.setFeedback(msg);
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(long userId, int FeedbackId) {
        feedbackRepository.delete(checkFeedback(userId, FeedbackId));
    }

    public boolean handleFeedback(long userId, int FeedbackId, boolean handle) {
        var feedback = checkFeedback(userId, FeedbackId);
        feedback.setHandle(handle);
        return feedbackRepository.save(feedback).isHandle();
    }

    private MapFeedback checkFeedback(long userId, int FeedbackId) {
        var feedbackOptional = feedbackRepository.findById(FeedbackId);
        if (feedbackOptional.isEmpty()) {
            throw new NotFoundException();
        }
        var feedback = feedbackOptional.get();

        if (!isAdminByPool(feedback.getItem().getCategory().getGroupId(), userId) && !feedback.getCreaterId().equals(userId)) {
            throw new PermissionException("非本人禁止修改/删除");
        }
        return feedback;
    }
}
