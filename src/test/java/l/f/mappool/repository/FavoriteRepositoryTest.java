package l.f.mappool.repository;

import jakarta.annotation.Resource;
import l.f.mappool.entity.Favorite;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FavoriteRepositoryTest {
    @Resource
    FavoriteRepository favoriteRepository;

    @Test
    void testTagsCreate() {
        favoriteRepository.save(new Favorite().setId(1).setBid(114514L).setUserId(3311L));
    }

    @Test
    void testTagsAdd() {
        favoriteRepository.addTags(1, "tag1", "tag2");
    }

    @Test
    void testTagsDelete() {
        favoriteRepository.deleteTags(1, "tag2");
    }

    @Test
    void testTagsReset() {
        favoriteRepository.replaceTags(1, "tag1", "tag2");
    }

    @Test
    void testDelete() {
        var f = favoriteRepository.findById(1);
        if (f.isEmpty()) return;
        favoriteRepository.delete(f.get());
    }

    @Test
    void testFindAll() {
        var l = favoriteRepository.searchUserAllByTags(856,  "tag2");
        System.out.println(l.size());
    }
}
