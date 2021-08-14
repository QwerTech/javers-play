package org.javers.organization.structure.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;

@MapperConfig(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface CentralConfig {
}