package ru.sidey383.twitch.dto.twitch;

import lombok.Data;

import java.util.List;

@Data
public class DataList<T> {

    private List<T> data;

}
