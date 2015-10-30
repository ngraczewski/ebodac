package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("lookupService")
public class LookupServiceImpl implements LookupService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private MDSLookupService mdsLookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> Records<T> getEntities(Class<T> entityType, String lookup,
                                      String lookupFields, QueryParams queryParams) {
        List<T> entities;
        long recordCount;
        int rowCount;
        QueryParams newQueryParams = queryParams;
        if (StringUtils.isNotBlank(lookup) && newQueryParams != null) {
            try {
                entities = mdsLookupService.findMany(entityType.getName(), lookup, getFields(lookupFields), newQueryParams);
                recordCount = mdsLookupService.count(entityType.getName(), lookup, getFields(lookupFields));
            } catch (IOException e) {
                throw new EbodacLookupException("Invalid lookup fields: " + lookupFields, e.getCause());
            }

            rowCount = getRowCount(newQueryParams, recordCount);

            if(newQueryParams.getPage() == null) {
                newQueryParams = new QueryParams(1, newQueryParams.getPageSize(), newQueryParams.getOrderList());
            }

            if (entities == null) {
                entities = new ArrayList<>();
            }

            return new Records<>(newQueryParams.getPage(), rowCount, (int) recordCount, entities);
        }

        recordCount = mdsLookupService.countAll(entityType.getName());

        int page;
        if(newQueryParams != null && newQueryParams.getPageSize() != null && newQueryParams.getPage() != null) {
            rowCount = (int) Math.ceil(recordCount / (double) newQueryParams.getPageSize());
            page = newQueryParams.getPage();
            entities = mdsLookupService.retrieveAll(entityType.getName(), newQueryParams);
        } else {
            rowCount = (int) recordCount;
            page = 1;
            entities = mdsLookupService.retrieveAll(entityType.getName(), newQueryParams);
        }

        if (entities == null) {
            entities = new ArrayList<>();
        }

        return new Records<>(page, rowCount, (int) recordCount, entities);
    }

    @Override
    public <T> Records<?> getEntities(Class<?> entityDtoType, Class<T> entityType, String lookup,
                                      String lookupFields, QueryParams queryParams) {
        List<Object> entityDtoList = new ArrayList<>();
        Records<T> baseRecords = getEntities(entityType, lookup, lookupFields, queryParams);
        Constructor reportDtoConstructor;
        try {
            reportDtoConstructor = entityDtoType.getConstructor(entityType);
        } catch (NoSuchMethodException e) {
            throw new EbodacLookupException("Invalid reportDtoType parametr", e);
        }
        try {
            for (T entity : baseRecords.getRows()) {
                Object entityDto;
                entityDto = reportDtoConstructor.newInstance(entity);
                entityDtoList.add(entityDto);
            }
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
           throw new EbodacLookupException("Can not create: " + entityDtoType.getName() +" using: " + entityType.getName(), e);
        }
        return new Records<>(baseRecords.getPage(), baseRecords.getTotal(), baseRecords.getRecords(), entityDtoList);
    }

    @Override
    public List<LookupDto> getAvailableLookups(String entityName) {
        EntityDto entity = getEntityByEntityClassName(entityName);
        AdvancedSettingsDto settingsDto = entityService.getAdvancedSettings(entity.getId(), true);
        return settingsDto.getIndexes();
    }

    private EntityDto getEntityByEntityClassName(String entityName) {
        EntityDto entity = entityService.getEntityByClassName(entityName);
        if(entity == null) {
            throw new EbodacLookupException( "Can not find entity named: " + entityName);
        }
        return entity;
    }

    private Map<String, Object> getFields(String lookupFields) throws IOException {
        return objectMapper.readValue(lookupFields, new TypeReference<HashMap>() {});
    }

    private int getRowCount(QueryParams newQueryParams, long recordCount) {
        if (newQueryParams.getPageSize() != null) {
            return (int) Math.ceil(recordCount / (double) newQueryParams.getPageSize());
        } else {
            return (int) recordCount;
        }
    }

}
