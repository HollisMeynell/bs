package l.f.mappool.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FavoriteDto {
    @NotNull
    Long bid;
    String info;
    String[] tags;
}
