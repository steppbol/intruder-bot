package com.ffanaticism.intruder.serviceprovider.util.mapper;

import com.ffanaticism.intruder.serviceprovider.entity.User;
import com.ffanaticism.intruder.serviceprovider.model.StoredUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePatchEntity(StoredUser source, @MappingTarget StoredUser target);

    @Mappings({
            @Mapping(target = "characteristics", source = "user.characteristics")
    })
    StoredUser toStoredUser(User user);
}
