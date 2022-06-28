package com.ffanaticism.intruder.serviceprovider.util.mapper;

import com.ffanaticism.intruder.serviceprovider.entity.Video;
import com.ffanaticism.intruder.serviceprovider.model.StoredVideo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatchEntity(StoredVideo source, @MappingTarget StoredVideo target);

    @Mappings({
            @Mapping(target = "url", source = "video.url"),
            @Mapping(target = "characteristics", source = "video.characteristics")
    })
    StoredVideo toStoredVideo(Video video);
}
