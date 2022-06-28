package com.ffanaticism.intruder.serviceprovider.util.mapper;

import com.ffanaticism.intruder.serviceprovider.entity.Image;
import com.ffanaticism.intruder.serviceprovider.model.StoredImage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatchEntity(StoredImage source, @MappingTarget StoredImage target);

    @Mappings({
            @Mapping(target = "url", source = "image.url"),
            @Mapping(target = "characteristics", source = "image.characteristics")
    })
    StoredImage toStoredImage(Image image);
}
