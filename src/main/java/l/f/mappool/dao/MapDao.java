package l.f.mappool.dao;

import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.entity.osu.BeatMapSet;
import l.f.mappool.repository.osu.BeatMapRepository;
import l.f.mappool.repository.osu.BeatMapSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@SuppressWarnings("unused")
public class MapDao {
    BeatMapRepository beatMapRepository;
    BeatMapSetRepository beatMapSetRepository;
    @Autowired
    public MapDao(BeatMapRepository beatMapRepository, BeatMapSetRepository beatMapSetRepository) {
        this.beatMapRepository = beatMapRepository;
        this.beatMapSetRepository = beatMapSetRepository;
    }

    public <T extends BeatMap>T saveBeatMap(T entity) {
        BeatMapSet set;
        if ((set = entity.getBeatMapSet()) != null) {
            beatMapSetRepository.save(set);
        }
        beatMapRepository.save(entity);
        return entity;
    }

    public Optional<BeatMap> getFirstBeatMap(long id) {

        return beatMapRepository.findBeatMapById(id);
    }
}
