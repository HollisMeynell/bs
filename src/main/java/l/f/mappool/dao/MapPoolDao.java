package l.f.mappool.dao;

import jakarta.persistence.EntityManager;
import l.f.mappool.entity.pool.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.enums.PoolStatus;
import l.f.mappool.exception.HttpError;
import l.f.mappool.exception.NotFoundException;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.pool.*;
import l.f.mappool.vo.PoolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@SuppressWarnings("unused")
public class MapPoolDao {
    private final PoolRepository poolRepository;
    private final FeedbackRepository feedbackRepository;
    private final PoolUserRepository poolUserRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryItemRepository categoryItemRepository;
    private final CategoryGroupRepository categoryGroupRepository;

    @SuppressWarnings("all")
    private final EntityManager entityManager;

    @Autowired
    public MapPoolDao(PoolRepository poolRepository,
                      PoolUserRepository poolUserRepository,
                      CategoryRepository categoryRepository,
                      CategoryGroupRepository categoryGroupRepository,
                      CategoryItemRepository categoryItemRepository,
                      FeedbackRepository feedbackRepository,
                      EntityManager entityManager
    ) {
        this.poolRepository = poolRepository;
        this.poolUserRepository = poolUserRepository;
        this.categoryRepository = categoryRepository;
        this.categoryGroupRepository = categoryGroupRepository;
        this.categoryItemRepository = categoryItemRepository;
        this.feedbackRepository = feedbackRepository;
        this.entityManager = entityManager;
    }

    /* ************************************************* Pool **************************************************************** */

    /***
     * 创建
     * @return 新 MapPool
     */
    public Pool createPool(long userId, String poolName, String banner, String info) {
        var map = new Pool();
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

    public List<Pool> queryByName(String name, long userId, int page, int size) {
        return poolRepository.queryByName(name, userId, PageRequest.of(page, size));
    }

    public int countByName(String name, long userId) {
        return poolRepository.countByName(name, userId);
    }

    public Optional<Pool> queryById(int id) {
        return poolRepository.getByIdNotDelete(id);
    }

    public Page<Pool> getPublicPool(Pageable pageable) {
        return poolRepository.getAllOpenPool(pageable);
    }

    /***
     * 获取用户下所有可见的图池
     * @param userId uid
     * @return 权限+图池
     */
    public Map<PoolPermission, List<Pool>> getAllPool(long userId) {
        var map = new HashMap<PoolPermission, List<Pool>>();
        var ulist = poolUserRepository.searchAllByUserId(userId);

        // 过滤掉删除 / 截止的图池
        var uCreates = ulist.stream().filter(u -> u.getPermission() == PoolPermission.CREATE)
                .map(PoolUser::getPool).filter(pool -> !pool.getStatus().equals(PoolStatus.DELETE)).toList();
        var uAdmins = ulist.stream().filter(u -> u.getPermission() == PoolPermission.ADMIN)
                .map(PoolUser::getPool).filter(pool -> !pool.getStatus().equals(PoolStatus.DELETE)).toList();
        var uChoosers = ulist.stream().filter(u -> u.getPermission() == PoolPermission.CHOOSER)
                .map(PoolUser::getPool)
                .filter(pool -> !(pool.getStatus().equals(PoolStatus.DELETE) || pool.getStatus().equals(PoolStatus.STOP)))
                .toList();
        var uTesters = ulist.stream().filter(u -> u.getPermission() == PoolPermission.TESTER)
                .map(PoolUser::getPool)
                .filter(pool -> !(pool.getStatus().equals(PoolStatus.DELETE) || pool.getStatus().equals(PoolStatus.STOP)))
                .toList();

        map.put(PoolPermission.CREATE, uCreates);
        map.put(PoolPermission.ADMIN, uAdmins);
        map.put(PoolPermission.CHOOSER, uChoosers);
        map.put(PoolPermission.TESTER, uTesters);

        return map;
    }

    public List<Pool> getAllPublicPool() {
        return poolRepository.getAllOpenPool();
    }

    public List<Pool> getAllPublicPoolExcludeUser(long uid) {
        return poolRepository.getAllOpenPool();
    }

    public List<Pool> getAllMarkPool(long uid) {
        return poolRepository.queryByUserMark(uid);
    }

    public Optional<Pool> getMapPoolById(int id) {
        return poolRepository.getByIdNotDelete(id);
    }

    public Pool saveMapPool(Pool pool) {
        return poolRepository.saveAndFlush(pool);
    }

    /***
     * 真删除
     * @param uid uid
     */
    public void removePool(long uid, Pool pool) {
        poolUserRepository.deleteByPool(pool);
        poolRepository.delete(pool);
    }

    public void deletePool(long uid, Pool pool) {
        pool.setStatus(PoolStatus.DELETE);
        poolRepository.save(pool);
    }

    public PoolVo exportPool(Pool pool) throws HttpError {
        for (var group : pool.getGroups()) {
            for (var category : group.getCategories()) {
                testCategory(category);
            }
        }

        pool.setStatus(PoolStatus.SHOW);
        var nPool = poolRepository.save(pool);
        return getExportPool(nPool);
    }

    public PoolVo getExportPool(int poolId) throws HttpError {
        var poolOpt = poolRepository.getByIdNotDelete(poolId);
        if (poolOpt.isEmpty()) {
            throw new NotFoundException();
        }
        return getExportPool(poolOpt.get());
    }

    public PoolVo getExportPool(Pool pool) throws HttpError {
        if (!pool.getStatus().equals(PoolStatus.SHOW)) {
            throw new HttpError(403, "尝试导出未公开的图池");
        }
        return new PoolVo(pool);
    }

    private void testCategory(PoolCategory category) throws HttpError {
        if (category.getChosed() == null) {
            throw new HttpError(403, "包含未确认的位置: " + category.getName());
        }
    }

    /************************************************** User *****************************************************************/

    public int queryCountById(int id) {
        return poolRepository.getCountById(id);
    }

    public PoolUser addCreatUser(long userId, long addUserId, int poolId) {
        return addUser(addUserId, poolId, PoolPermission.CREATE);
    }

    public PoolUser addAdminUser(long userId, long addUserId, int poolId) {
        return addUser(addUserId, poolId, PoolPermission.ADMIN);
    }


    public PoolUser addChooserUser(long userId, long addUserId, int poolId) {
        return addUser(addUserId, poolId, PoolPermission.CHOOSER);
    }

    public PoolUser addTesterUser(long userId, long addUserId, int poolId) {
        return addUser(addUserId, poolId, PoolPermission.TESTER);
    }

    public void deleteUser(long userId, long deleteUserId, int poolId) {
        var u = poolUserRepository.getMapPoolUserByPoolIdAndUserId(poolId, deleteUserId);
        if (u.isEmpty()) {
            throw new NotFoundException();
        }
        poolUserRepository.delete(u.get());
    }

    private boolean testBef(Optional<PoolPermission> permission, PoolPermission... poolPermissions) {
        if (permission.isEmpty()) {
            return false;
        }
        if (poolPermissions.length == PoolPermission.values().length) {
            return true;
        }
        var user = permission.get();
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
        var permission = poolUserRepository.getMapPoolUserPermission(poolId, userId);
        return testBef(permission, poolPermissions);
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
        var permission = poolUserRepository.getMapPoolUserPermissionByGroupIdAndUserId(groupId, userId);
        return testBef(permission, poolPermissions);
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
        var permission = poolUserRepository.getMapPoolUserPermission(categoryId, userId);
        return testBef(permission, PoolPermission.CREATE, PoolPermission.ADMIN, PoolPermission.CHOOSER);
    }

    public boolean isUserByGroup(int groupId, long userId) {
        return testPermissionByCategoryGroup(groupId, userId, PoolPermission.values());
    }

    public PoolUser addUser(long addUserId, int poolId, PoolPermission permission) {
        var u = poolUserRepository.getMapPoolUserByPoolIdAndUserId(poolId, addUserId);
        PoolUser addUser;
        if (u.isPresent()) {
            addUser = u.get();
            addUser.setPermission(permission);
        } else {
            addUser = new PoolUser();
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
    public PoolCategoryGroup createCategoryGroup(long userId, int poolId, String name, String info, int color) {
        var mg = new PoolCategoryGroup();
        mg.setPoolId(poolId);
        mg.setColor(color);
        mg.setName(name);
        mg.setInfo(info);
        mg.setSort(0);
        return categoryGroupRepository.save(mg);
    }

    public Optional<PoolCategoryGroup> getCategoryGroupById(int groupId) {
        return categoryGroupRepository.findById(groupId);
    }

    public PoolCategoryGroup saveMapCategoryGroup(PoolCategoryGroup group) {
        return categoryGroupRepository.saveAndFlush(group);
    }

    public List<PoolCategoryGroup> getAllCategotys(int poolId) {
        return categoryRepository.getAllCategory(poolId);
    }

    public void deleteMapCategoryGroup(PoolCategoryGroup group) {
        categoryGroupRepository.delete(group);
    }

    /* ************************************************* Category **************************************************************** */

    /**
     * 创建具体分类 比如NM1
     *
     * @param groupId CategoryGroup.id
     */
    public PoolCategory createCategory(long userId, int groupId, String categoryName) {
        var category = new PoolCategory();
        category.setGroupId(groupId);
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    public PoolCategory saveCategory(PoolCategory category) {
        return categoryRepository.saveAndFlush(category);
    }

    public void deleteCategory(PoolCategory category) {
        for (var item : category.getItems()) {
            deleteCategoryItem(item);
        }
        categoryRepository.delete(category);
    }

    public Optional<PoolCategory> getMapCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /* ************************************************* Item **************************************************************** */

    /***
     * 加一张图
     * @return 包含推荐人id, bid, 描述信息的结构
     */
    public PoolCategoryItem createCategoryItem(long userId, int itemId, long bid, String info) {
        var categoryOpt = categoryRepository.findById(itemId);
        if (categoryOpt.isEmpty()) {
            throw new NotFoundException();
        }
        var category = categoryOpt.get();
        var categoryItem = new PoolCategoryItem();
        categoryItem.setSort(0);
        categoryItem.setInfo(info);
        categoryItem.setChous(bid);
        categoryItem.setCreaterId(userId);
        categoryItem.setCategoryId(itemId);
        return categoryItemRepository.save(categoryItem);
    }

    public PoolCategoryItem updateCategoryItem(PoolCategoryItem item, long bid, String info, int sort) {

        item.setChous(bid);
        item.setSort(sort);
        item.setInfo(info);

        return categoryItemRepository.save(item);
    }

    public void deleteCategoryItem(PoolCategoryItem item) {
        if (item.getFeedbacks().size() != 0) {
            feedbackRepository.deleteAll(item.getFeedbacks());
        }
        categoryItemRepository.delete(item);
    }

    public PoolCategoryItem checkItem(long userId, int itemId) {
        var categoryItemOpt = categoryItemRepository.findById(itemId);
        if (categoryItemOpt.isEmpty()) throw new NotFoundException();
        var item = categoryItemOpt.get();

        if (!isAdminByGroup(item.getCategory().getGroupId(), userId) && !item.getCreaterId().equals(userId)) {
            throw new PermissionException("非创建者无法操作");
        }

        return item;
    }

    public Optional<PoolCategoryItem> getMapCategoryItemById(int itemId) {
        return categoryItemRepository.findById(itemId);
    }

    public List<PoolCategoryItem> getAllCategoryItems(int categoryId) {
        var selectAttr = new PoolCategory();
        selectAttr.setId(categoryId);
        return categoryItemRepository.findAllByCategory(selectAttr);
    }

    /* ************************************************* Other **************************************************************** */

    public PoolFeedback createFeedback(long userId, int categoryItemId, @Nullable Boolean agree, String msg) {
        var categoryItem = categoryItemRepository.findById(categoryItemId);
        if (categoryItem.isEmpty()) {
            throw new NotFoundException();
        }
        var category = categoryItem.get().getCategory();
        if (!isUserByGroup(category.getGroupId(), userId)) {
            throw new PermissionException();
        }
        var feedback = new PoolFeedback();
        feedback.setAgree(agree);
        feedback.setFeedback(msg);
        feedback.setCreaterId(userId);
        feedback.setItemId(categoryItemId);
        return feedbackRepository.save(feedback);
    }

    public PoolFeedback updateFeedback(PoolFeedback feedback, @Nullable Boolean agree, String msg) {
        feedback.setAgree(agree);
        feedback.setFeedback(msg);
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(PoolFeedback feedback) {
        feedbackRepository.delete(feedback);
    }

    public PoolFeedback handleFeedback(PoolFeedback feedback, boolean handle) {
        feedback.setHandle(handle);
        return feedbackRepository.save(feedback);
    }

    public PoolFeedback checkFeedback(long userId, int FeedbackId) {
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
