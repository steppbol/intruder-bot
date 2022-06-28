package com.ffanaticism.intruder.serviceprovider.util.mapper;

import com.ffanaticism.intruder.serviceprovider.entity.Audio;
import com.ffanaticism.intruder.serviceprovider.model.StoredAudio;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AudioMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatchEntity(StoredAudio source, @MappingTarget StoredAudio target);

    @Mappings({
            @Mapping(target = "url", source = "audio.url"),
            @Mapping(target = "characteristics", source = "audio.characteristics")
    })
    StoredAudio toStoredAudio(Audio audio);
}
