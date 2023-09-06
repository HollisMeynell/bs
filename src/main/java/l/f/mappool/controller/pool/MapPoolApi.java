package l.f.mappool.controller.pool;

import l.f.mappool.controller.PoolApi;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.map.MarkPoolDto;
import l.f.mappool.dto.map.PoolUserDto;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.dto.validator.AddUser;
import l.f.mappool.dto.validator.mapPool.CreatePool;
import l.f.mappool.dto.validator.mapPool.DeletePool;
import l.f.mappool.dto.validator.mapPool.SetPool;
import l.f.mappool.entity.MapPool;
import l.f.mappool.entity.MapPoolUser;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class MapPoolApi extends PoolApi {

    /**
     * 搜素图池,公开或者属于自己的图池
     *
     * @param queryMapPoolDto 名字或id
     * @return 图池
     */
    @GetMapping("queryPublic")
    DataListVo<MapPool> query(@Validated QueryMapPoolDto queryMapPoolDto) {
        var u = ContextUtil.getContextUser();
        var allCount = mapPoolService.countByNameAndId(queryMapPoolDto, u.getOsuId());
        var data = mapPoolService.queryByNameAndId(queryMapPoolDto, u.getOsuId());
        return new DataListVo<MapPool>()
                .setData(data)
                .setTotalItems(allCount)
                .setTotalPages(allCount / queryMapPoolDto.getPageSize() + allCount % queryMapPoolDto.getPageSize() == 0 ? 0 : 1)
                .setPageSize(queryMapPoolDto.getPageSize())
                .setCurrentPage(queryMapPoolDto.getPageNum());
    }

    /**
     * 查询所有自己参与的图池
     *
     * @return 图池
     */
    @GetMapping("getMyPool")
    DataVo<Map<PoolPermission, List<MapPool>>> getAllPool() {
        var u = ContextUtil.getContextUser();
        return new DataVo<>(mapPoolService.getAllPool(u.getOsuId()));
    }

    /**
     * 创建图池
     *
     * @param create 包含 name, banner(文件key) info
     * @return 创建结果
     */
    @PutMapping("pool")
    DataVo<MapPool> createPool(@RequestBody @Validated(CreatePool.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var pool = mapPoolService.createMapPool(u.getOsuId(), create.getName(), create.getBanner(), create.getInfo());
        return new DataVo<>("创建成功", pool);
    }

    /**
     * 修改图池
     * @param poolDto 要修改的信息
     */
    @PatchMapping("pool")
    DataVo<MapPool> setPool(@RequestBody @Validated(SetPool.class) MapPoolDto poolDto) {
        var u = ContextUtil.getContextUser();
        var pool = mapPoolService.updateMapPool(u.getOsuId(), poolDto.getPoolId(), poolDto.getName(), poolDto.getBanner(), poolDto.getInfo());
        return new DataVo<>("创建成功", pool);
    }

    @DeleteMapping("pool")
    DataVo<String> deletePool(@Validated(DeletePool.class) MapPoolDto poolDto) {
        var u = ContextUtil.getContextUser();
        mapPoolService.deleteMapPool(u.getOsuId(), poolDto.getPoolId());
        return new DataVo<>("删除成功", null);
    }

    @DeleteMapping("removePool")
    DataVo<String> removePool(@Validated(DeletePool.class) MapPoolDto poolDto) {
        var u = ContextUtil.getContextUser();
        mapPoolService.removePool(u.getOsuId(), poolDto.getPoolId());
        return new DataVo<>("删除完成", null);
    }


    /**
     * 标记为常用图池
     * @param mark 参数 poolId
     */
    @PutMapping("mark")
    DataVo<Boolean> addPoolMark(@RequestBody @Validated MarkPoolDto mark) {
        var u = ContextUtil.getContextUser();
        mapPoolService.addMarkPool(u.getOsuId(), mark.getPoolId());
        return new DataVo<>(Boolean.TRUE).setMessage("创建成功");
    }

    @DeleteMapping("mark")
    DataVo<Boolean> deletePoolMark(@Validated MarkPoolDto mark) {
        var u = ContextUtil.getContextUser();
        int sum = mapPoolService.deleteMarkPool(u.getOsuId(), mark.getPoolId());
        if (sum > 0) {
            return new DataVo<>(Boolean.TRUE).setMessage("删除成功");
        } else {
            return new DataVo<>(Boolean.FALSE).setCode(201).setMessage("无图池记录");
        }
    }


    /**
     * 查询标记的表
     */
    @GetMapping("mark")
    DataListVo<MapPool> getUserMarkPool() {
        var u = ContextUtil.getContextUser();
        return mapPoolService.getAllMarkPool(u.getOsuId());
    }

    @PutMapping("addAdmin")
    DataVo<MapPoolUser> addAdminUser(@RequestBody @Validated(AddUser.class) PoolUserDto user) {
        var u = ContextUtil.getContextUser();
        return mapPoolService.addAdminUser(u.getOsuId(), user.getUserId(), user.getPoolId());
    }
    @PutMapping("addChooser")
    DataVo<MapPoolUser> addChooserUser(@RequestBody @Validated(AddUser.class) PoolUserDto user) {
        var u = ContextUtil.getContextUser();
        return mapPoolService.addChooserUser(u.getOsuId(), user.getUserId(), user.getPoolId());
    }
    @PutMapping("addTester")
    DataVo<MapPoolUser> addTesterUser(@RequestBody @Validated(AddUser.class) PoolUserDto user) {
        var u = ContextUtil.getContextUser();
        return mapPoolService.addTesterUser(u.getOsuId(), user.getUserId(), user.getPoolId());
    }

    @PutMapping("deleteUser")
    DataVo<MapPoolUser> deleteUser(@RequestBody @Validated(AddUser.class) PoolUserDto user) {
        var u = ContextUtil.getContextUser();
        return mapPoolService.deleteUser(u.getOsuId(), user.getUserId(), user.getPoolId());
    }
}
