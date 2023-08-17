package l.f.mappool.controller;

import jakarta.annotation.Resource;
import l.f.mappool.dto.map.CategoryItemDto;
import l.f.mappool.dto.map.MapPoolDto;
import l.f.mappool.dto.map.MarkPoolDto;
import l.f.mappool.dto.map.QueryMapPoolDto;
import l.f.mappool.dto.validator.mapPool.CreateCategory;
import l.f.mappool.dto.validator.mapPool.CreateCategoryGroup;
import l.f.mappool.dto.validator.mapPool.CreatePool;
import l.f.mappool.dto.validator.mapPool.SetPool;
import l.f.mappool.entity.*;
import l.f.mappool.enums.PoolPermission;
import l.f.mappool.service.MapPoolService;
import l.f.mappool.service.OsuGetService;
import l.f.mappool.util.ContextUtil;
import l.f.mappool.vo.DataListVo;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.FavoritesLiteVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@CrossOrigin
@ResponseBody
@RequestMapping(value = "/api/map", produces = "application/json;charset=UTF-8")
public class MapApi {
    @Resource
    OsuGetService osuService;
    @Resource
    MapPoolService mapPoolService;

    /**
     * 搜素图池,公开或者属于自己的图池
     * @param queryMapPoolDto 名字或id
     * @return 图池
     */
    @GetMapping("/queryPublic")
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
     *  查询所有自己参与的图池
     * @return 图池
     */
    @GetMapping("getMyPool")
    DataVo<Map<PoolPermission, List<MapPool>>> getAllPool() {
        var u = ContextUtil.getContextUser();
        return new DataVo<>(mapPoolService.getAllPool(u.getOsuId()));
    }

    /**
     * 获取组别的详细信息
     * @param id 组别id
     * @return 组别信息
     */
    @GetMapping("getGroup")
    DataListVo<MapCategoryGroup> getGroup(int id) {
        var list = mapPoolService.getCategoryGroup(id);
        return new DataListVo<MapCategoryGroup>().setData(list).setTotalItems(list.size());
    }

    /**
     * 创建图池
     * @param create 包含 name, banner(文件key) info
     * @return 创建结果
     */
    @PutMapping("createPool")
    DataVo<MapPool> createPool(@RequestBody @Validated(CreatePool.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var pool = mapPoolService.createMapPool(u.getOsuId(), create.getName(), create.getBanner(), create.getInfo());
        return new DataVo<>("创建成功", pool);
    }


    @PutMapping("setPoolInfo")
    DataVo<MapPool> setPoolInfo(@RequestBody @Validated(SetPool.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var pool = mapPoolService.createMapPool(u.getOsuId(), create.getName(), create.getBanner(), create.getInfo());
        return new DataVo<>("创建成功", pool);
    }

    @PutMapping("createCategoryGroup")
    DataVo<MapCategoryGroup> createCategoryGroup(@RequestBody @Validated(CreateCategoryGroup.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var group = mapPoolService.createCategoryGroup(u.getOsuId(), create.getPoolId(), create.getName(), create.getInfo(), create.getColor());
        return new DataVo<>("创建成功", group);
    }

    @PutMapping("createCategory")
    DataVo<MapCategory> createCategory(@RequestBody @Validated(CreateCategory.class) MapPoolDto create) {
        var u = ContextUtil.getContextUser();
        var category = mapPoolService.createCategory(u.getOsuId(), create.getGroupId(), create.getName());
        return new DataVo<>("创建成功", category);
    }

    @PutMapping("createCategoryItem")
    DataVo<MapCategoryItem> createItem(@RequestBody @Validated() CategoryItemDto create) {
        var u = ContextUtil.getContextUser();
        var group = mapPoolService.createCategoryItem(u.getOsuId(), create.getCategoryId(), create.getBeatmapId(), create.getInfo());
        return new DataVo<>("创建成功", group);
    }


    @PutMapping("addMark")
    DataVo addPoolMark(@RequestBody @Validated MarkPoolDto mark) {
        var u = ContextUtil.getContextUser();
        mapPoolService.addMarkPool(u.getOsuId(), mark.getPoolid());
        return new DataVo<>().setMessage("创建成功");
    }

    @DeleteMapping("deleteMark")
    DataVo deletePoolMark(@Validated MarkPoolDto mark) {
        var u = ContextUtil.getContextUser();
        int sum = mapPoolService.deleteMarkPool(u.getOsuId(), mark.getPoolid());
        if (sum > 0) {
            return new DataVo().setMessage("删除成功");
        } else {
            return new DataVo().setCode(201).setMessage("无图池记录");
        }
    }


    @GetMapping("getMark")
    DataListVo<MapPool> getUserMarkPool() {
        var u = ContextUtil.getContextUser();
        return mapPoolService.getAllMarkPool(u.getOsuId());
    }


    @GetMapping("/getMapInfo")
    DataListVo<FavoritesLiteVo> getMapInfo(@Validated @Nullable QueryMapPoolDto m) {
        var user = ContextUtil.getContextUser();
        return mapPoolService.getMapInfo();
    }

    @GetMapping("/getBeatMapInfo/{bid}")
    DataVo<BeatMap> getBeatmap(@PathVariable("bid") long bid) {
        return new DataVo<>(osuService.getMapInfoByDB(bid));
    }
}
