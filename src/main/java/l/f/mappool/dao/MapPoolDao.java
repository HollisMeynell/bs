package l.f.mappool.dao;

import l.f.mappool.entity.MapCategory;
import l.f.mappool.entity.MapCategoryItem;
import l.f.mappool.entity.MapPool;
import l.f.mappool.entity.MapPoolUser;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.MapCategoryRepository;
import l.f.mappool.repository.MapPoolRepository;
import l.f.mappool.repository.MapPoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MapPoolDao {
    private final MapPoolRepository poolRepository;
    private final MapPoolUserRepository poolUserRepository;
    private final MapCategoryRepository categoryRepository;

    @Autowired
    public MapPoolDao(MapPoolRepository mapPoolRepository, MapPoolUserRepository mapPoolUserRepository, MapCategoryRepository mapCategoryRepository) {
        poolRepository = mapPoolRepository;
        poolUserRepository = mapPoolUserRepository;
        categoryRepository = mapCategoryRepository;
    }

    public MapPool createPool(long userId, String poolName, String info) {
        var map = new MapPool();
        map.setName(poolName);
        map.setInfo(info);
        map = poolRepository.save(map);

        var admin = new MapPoolUser();
        admin.setPoolId(map.getId());
        admin.setUserId(userId);
        admin.setPermission(PoolPermission.CREATE);
        poolUserRepository.save(admin);
        return map;
    }

    public MapPoolUser addAdminUser(long userId, long addUserId, int poolId) {
        if (!isCreater(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.ADMIN);
    }


    public MapPoolUser addChooserUser(long userId, long addUserId, int poolId) {
        if (!isAdmin(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.CHOOSER);
    }

    public MapPoolUser addTesterUser(long userId, long addUserId, int poolId) {
        if (!isAdmin(poolId, userId)) {
            throw new PermissionException();
        }
        return addUser(addUserId, poolId, PoolPermission.TESTER);
    }

    public MapCategory createCategory(long userId, int poolId, String categoryName, String categoryType, int categoryColor) {
        if (!isAdmin(poolId, userId)) {
            throw new PermissionException();
        }

        var category = new MapCategory();
        category.setPoolId(poolId);
        category.setName(categoryName);
        category.setType(categoryType);
        category.setColor(categoryColor);

        category = categoryRepository.save(category);
        return category;
    }

    public MapCategoryItem createCategoryItem(long userId, int categoryId, String name, String type, String sorted,String info){
        var categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            // TODO throw exception
            return null;
        }
        var category = categoryOpt.get();
        if (!isAdmin(category.getPoolId(), userId)) {
            throw new PermissionException();
        }
        var categoryItem = new MapCategoryItem();
        categoryItem.setType(type);
        categoryItem.setName(name);
        categoryItem.setSorted(0);
        categoryItem.setInfo(info);
        // TODO
        return categoryItem;
    }

    public Map<PoolPermission, List<MapPool>> getAllPool(long userId) {
        var map = new HashMap<PoolPermission, List<MapPool>>();
        var ulist = poolUserRepository.searchAllByUserId(userId);
        var uCreate = ulist.stream().filter(u->u.getPermission() == PoolPermission.CREATE).map(MapPoolUser::getPoolId).toList();
        var uAdmin = ulist.stream().filter(u->u.getPermission() == PoolPermission.ADMIN).map(MapPoolUser::getPoolId).toList();
        var uChooser = ulist.stream().filter(u->u.getPermission() == PoolPermission.CHOOSER).map(MapPoolUser::getPoolId).toList();
        var uTester = ulist.stream().filter(u->u.getPermission() == PoolPermission.TESTER).map(MapPoolUser::getPoolId).toList();

        var plist = poolRepository.searchAllByUserId(userId);
        map.put(PoolPermission.CREATE, plist.stream().filter(p -> uCreate.contains(p.getId())).toList());
        map.put(PoolPermission.ADMIN, plist.stream().filter(p -> uAdmin.contains(p.getId())).toList());
        map.put(PoolPermission.CHOOSER, plist.stream().filter(p -> uChooser.contains(p.getId())).toList());
        map.put(PoolPermission.TESTER, plist.stream().filter(p -> uTester.contains(p.getId())).toList());

        return map;
    }

    private boolean testPermission(int poolId, long userId, PoolPermission... poolPermissions) {
        var userOpt = poolUserRepository.getMapPoolUserByPoolIdAndUserId(poolId, userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        var user = userOpt.get();
        for (var p : poolPermissions) {
            if (p == user.getPermission()) {
                return true;
            }
        }
        return false;
    }

    private boolean isCreater(int poolId, long userId) {
        return testPermission(poolId, userId, PoolPermission.CREATE);
    }

    private boolean isAdmin(int poolId, long userId) {
        return testPermission(poolId, userId, PoolPermission.CREATE, PoolPermission.ADMIN);
    }

    private boolean isChooser(int poolId, long userId) {
        return testPermission(poolId, userId, PoolPermission.CREATE, PoolPermission.ADMIN, PoolPermission.CHOOSER);
    }

    private MapPoolUser addUser (long addUserId, int poolId, PoolPermission permission) {
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
}
