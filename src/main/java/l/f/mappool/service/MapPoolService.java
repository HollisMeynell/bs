package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.dao.MapPoolDao;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.entity.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.exception.PermissionException;
import l.f.mappool.repository.MapPoolMark4UserRepository;
import l.f.mappool.repository.MapPoolUserRepository;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.FavoritesLiteVo;
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
    MapPoolUserRepository mapPoolUserRepository;
    @Resource
    UserService userService;

    @Resource
    MapPoolMark4UserRepository mapPoolMark4UserRepository;

    /***
     * 用户创建的图池数量到了最大值
     * @param user 用户
     * @return true: 超过了
     */
    public boolean isMax(OsuUser user) {
        int count = mapPoolUserRepository.getUserCreatedSize(user.getOsuId());
        return count > user.getMaxPoolSize();
    }

    public MapPool createMapPool(long userId, String name, String banner, String info) {
        var user = userService.getOsuUser(userId);
        if (this.isMax(user)) {
            throw new RuntimeException("to many");
        }
        var pool = mapPoolDao.createPool(user.getOsuId(), name, banner, info);
        return pool;
    }

    public MapPool updateMapPool(long userId, int poolId, String name, String banner, String info) {
        final MapPoolDao mapPoolDao1 = mapPoolDao;
        return null;
    }

    public MapCategoryItem createCategoryItem(long uid, int categoryId, long bid, String info) {

        if (!mapPoolDao.isChooserByCategory(categoryId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategoryItem(uid, categoryId, bid, info);
    }

    /***
     * 具体分类 比如 NM1,NM2 这种
     * @param uid
     * @param groupId
     * @param name
     * @return
     */
    public MapCategory createCategory(long uid, int groupId, String name) {
        if (!mapPoolDao.isAdminByGroup(groupId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategory(uid, groupId, name);
    }

    /***
     * 创建一个分类组,比如 NM,HD 这种
     * @param uid
     * @param poolId
     * @param name
     * @param info
     * @param color
     * @return
     */
    public MapCategoryGroup createCategoryGroup(long uid, int poolId, String name, String info, int color) {
        if (!mapPoolDao.isAdminByPool(poolId, uid)) {
            throw new PermissionException();
        }
        return mapPoolDao.createCategoryGroup(uid, poolId, name, info, color);
    }

    public Map<PoolPermission, List<MapPool>> getAllPool(long osuId) {
        return mapPoolDao.getAllPool(osuId);
    }

    public List<MapCategoryGroup> getCategoryGroup(int id) {
        return mapPoolDao.getAllCategotys(id);
    }

    public List<MapPool> queryByNameAndId(QueryMapPoolDto query, long userId) {

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
        var f = new MapPoolMark4User();
        f.setUid(uid);
        f.setPid(pid);
        mapPoolMark4UserRepository.saveAndFlush(f);
    }

    public int deleteMarkPool(long uid, int pid) {
        return mapPoolMark4UserRepository.deleteAllByUidaAndPid(uid, pid);
    }

    public DataListVo<MapPool> getAllMarkPool(long uid) {
        var list = mapPoolDao.getAllMarkPool(uid);
        return new DataListVo<MapPool>()
                .setTotalItems(list.size())
                .setData(list);
    }

    /***
     * 查收藏的
     * @return 收藏列表
     */

    public DataListVo<FavoritesLiteVo> getMapInfo() {
        List<FavoritesLiteVo> list = null;
        return new DataListVo<FavoritesLiteVo>().setData(list).setPageSize(0);
    }
}
