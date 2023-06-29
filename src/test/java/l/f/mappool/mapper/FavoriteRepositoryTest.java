package l.f.mappool.mapper;

import l.f.mappool.repository.FavoriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FavoriteRepositoryTest {
    Logger log = LoggerFactory.getLogger(FavoriteRepositoryTest.class);
    @Autowired
    FavoriteRepository favoriteRepository;
    @Test
    void allTags() {
        /*
        var n= new Notation();
        n.setId(1);
        n.setBid(15564L);
        n.setInfo("fasd");
        n.setUserId(114514L);
        n.setTags(new String[]{"xxc", "xxv", "xxb"});
        notationRepository.save(n);
        n.setId(2);
        n.setBid(15566L);
        n.setInfo("hello");
        n.setTags(new String[]{"nihk", "fuck"});
        n.setCreated(LocalDateTime.now());
        notationRepository.save(n);
        */

        var s = favoriteRepository.allUserTags(114514L);
        log.info(()->String.join(",", s));

        var nold = favoriteRepository.findByBidAndUserId(15564L, 114514L);
        log.info(()-> nold.isPresent()?"have":"no");

        var ts = favoriteRepository.searchAllByTags("nihk");
        log.info(()-> {
            if (ts.isEmpty())
                return "empty";
            return ts.get(0).getId() + ts.get(0).getInfo();
        });

    }
}