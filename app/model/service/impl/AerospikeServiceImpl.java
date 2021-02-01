package model.service.impl;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import model.core.GenericVOWrapper;
import model.core.LoadAerospikeConfigRequest;
import model.exception.*;
import model.helper.SetterGetterHelper;
import model.service.AerospikeEntity;
import model.service.IAerospikeService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AerospikeServiceImpl implements IAerospikeService {

        public static final WritePolicy                         policy             = new WritePolicy();

    public Map<Class<? extends AerospikeEntity>, GenericVOWrapper> markerToWrapperMap = new HashMap<Class<? extends AerospikeEntity>, GenericVOWrapper>();

    private static AerospikeClient                           client;

    public static ClientPolicy                              clientPolicy;

    @Override
    public void put(AerospikeEntity vo) throws PutFailedException {
        GenericVOWrapper voWrapper = markerToWrapperMap.get(vo.getClass());
        Bin[] bins = new Bin[voWrapper.getBinNameToGetterMap().size()];
        int index = 0;
        for (Entry<String, Method> e : voWrapper.getBinNameToGetterMap().entrySet()) {
            try {
                bins[index] = new Bin(e.getKey(), Value.get(e.getValue().invoke(vo, new Object[] {})));
            } catch (Exception e1) {
                throw new PutFailedException("Failed to invoke getter method on VO", e1);
            }
            index++;
        }
        Key key = null;
        try {
            key = new Key(voWrapper.getNamespace(), voWrapper.getSet(), Value.get(voWrapper.getBinNameToGetterMap().get(voWrapper.getKeyBinName()).invoke(vo, new Object[] {})));
        } catch (Exception e1) {
            throw new PutFailedException("Failed to invoke getter method on VO while fetching key", e1);
        }
        try {
            client.put(policy, key, bins);
        } catch (AerospikeException e1) {
            throw new PutFailedException("Failed while putting VO in Aerospike", e1);
        } catch (Throwable t) {
            throw new PutFailedException("Throwable encountered while putting VO in Aerospike", t);
        }
    }

    @Override
    public AerospikeEntity get(String key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Integer key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Long key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Float key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Byte key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Double key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Character key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    @Override
    public AerospikeEntity get(Boolean key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        return getVOFromRecord(key, clazz);
    }

    private AerospikeEntity getVOFromRecord(Object key, Class<? extends AerospikeEntity> clazz) throws GetFailedException {
        GenericVOWrapper voWrapper = markerToWrapperMap.get(clazz);
        Record record = null;
        try {
            if(key.getClass().equals(Long.class)){
                record = client.get(policy, new Key(voWrapper.getNamespace(), voWrapper.getSet(), (Long) key));
            }
            else{
                record = client.get(policy, new Key(voWrapper.getNamespace(), voWrapper.getSet(), (String) key));
            }
        } catch (AerospikeException e2) {
            throw new GetFailedException("AerospikeException", e2);
        } catch (Throwable t) {
            throw new GetFailedException("Throwable encountered", t);
        }
        AerospikeEntity vo = null;
        if (record != null) {
            try {
                vo = (AerospikeEntity) clazz.newInstance();
            } catch (InstantiationException e1) {
                throw new GetFailedException("Failed to instantiate VO", e1);
            } catch (IllegalAccessException e1) {
                throw new GetFailedException("Failed to instantiate VO", e1);
            }
            for (Entry<String, Method> e : voWrapper.getBinNameToSetterMap().entrySet()) {
                try {
                    if (e.getValue().getParameterTypes()[0].equals(Long.class)) {
                        e.getValue().invoke(vo, record.getLong(e.getKey()));
                    }else if(e.getValue().getParameterTypes()[0].equals(Integer.class)){
                        Object objValue=record.getValue(e.getKey());
                        if(objValue!=null&&!objValue.toString().isEmpty())
                            e.getValue().invoke(vo, Integer.valueOf(objValue.toString()));
                        else
                            e.getValue().invoke(vo, objValue);
                    }else {
                        e.getValue().invoke(vo, record.getValue(e.getKey()));
                    }
                } catch (Exception e1) {
                    throw new GetFailedException("Failed to invoke setter on VO", e1);
                }
            }
        }
        return vo;
    }

    @Override
    public boolean remove(String key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void loadAerospikeConfig(LoadAerospikeConfigRequest request) throws LoadFailedException {
        try {
            initializeClient(request);
            for (Class clazz : model.helper.ClassReader.getClasses(request.getPackageToScan())) {
                model.annotation.Set setAnnotation = (model.annotation.Set) clazz.getAnnotation(model.annotation.Set.class);
                if (setAnnotation != null) {
                    GenericVOWrapper voWrapper = new GenericVOWrapper();
                    voWrapper.setNamespace(setAnnotation.namespace());
                    voWrapper.setSet(setAnnotation.name());
                    for (Field f : clazz.getDeclaredFields()) {
                        model.annotation.Bin binAnnotation = (model.annotation.Bin) f.getAnnotation(model.annotation.Bin.class);
                        if (binAnnotation != null) {
                            if (voWrapper.getBinNameToGetterMap().put(binAnnotation.name(), clazz.getMethod(SetterGetterHelper.getGetterName(f), new Class[] {})) != null) {
                                throw new DuplicateBinNameException("Duplicate bin Name: " + binAnnotation.name());
                            }
                            if (voWrapper.getBinNameToSetterMap().put(binAnnotation.name(), clazz.getMethod(SetterGetterHelper.getSetterName(f), new Class[] { f.getType() })) != null) {
                                throw new DuplicateBinNameException("Duplicate bin Name: " + binAnnotation.name());
                            }
                        }
                        model.annotation.Key keyAnnotation = (model.annotation.Key) f.getAnnotation(model.annotation.Key.class);
                        if (keyAnnotation != null) {
                            voWrapper.setKeyBinName(binAnnotation.name());
                        }
                    }
                    if (model.helper.StringUtils.isEmpty(voWrapper.getKeyBinName())) {
                        throw new KeyNotDefinedException("No Key defined for Set: " + setAnnotation.name() + " in namespace: " + setAnnotation.namespace());
                    }
                    markerToWrapperMap.put(clazz, voWrapper);
                }
            }
        } catch (Exception e) {
            throw new LoadFailedException(e);
        }
    }

    private void initializeClient(LoadAerospikeConfigRequest request) throws Exception {
        List<String> endPointSplit = model.helper.StringUtils.split(request.getEndPoint(), ":");
        clientPolicy = new ClientPolicy();
        clientPolicy.failIfNotConnected = request.isFailIfNotConnected();
        clientPolicy.maxSocketIdle = request.getMaxSocketIdle();
        clientPolicy.maxThreads = request.getMaxThreads();
        clientPolicy.timeout = request.getTimeout();
        client = new AerospikeClient(clientPolicy, new Host(endPointSplit.get(0).trim(), Integer.parseInt(endPointSplit.get(1).trim())));
    }

    @Override
    public boolean remove(Long key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Integer key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Float key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Byte key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Double key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Boolean key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    @Override
    public boolean remove(Character key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        return removeVOForRecord(key, clazz);
    }

    private boolean removeVOForRecord(Object key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException {
        GenericVOWrapper voWrapper = markerToWrapperMap.get(clazz);
        try {
            return client.delete(policy, new Key(voWrapper.getNamespace(), voWrapper.getSet(), Value.get(key)));
        } catch (AerospikeException aex) {
            throw new RemoveFailedException("Failed to remove key: " + key, aex);
        } catch (Throwable t) {
            throw new RemoveFailedException("Throwable encountered while removing key: " + key, t);
        }
    }

    public RecordSet executeQuery(String namespace, String set, Filter filter,String...bins) throws AerospikeException {
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setSetName(set);
        statement.setBinNames(bins);
        statement.setFilters(filter);
        RecordSet rs = client.query(null, statement);
        return rs;
    }

    public RecordSet executeQuery(String namespace, String set, String[] bins,Filter...filter) throws AerospikeException {
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setSetName(set);
        statement.setBinNames(bins);
        statement.setFilters(filter);
        RecordSet rs = client.query(null, statement);
        return rs;
    }

    public AerospikeClient getClient(){
        return client;
    }

    public boolean exists(Key key) throws KeyNotDefinedException{
        try {
            return client.exists(policy,key);
        } catch (AerospikeException e) {
            throw new KeyNotDefinedException();
        }
    }

    public Record get(Key key) throws KeyNotDefinedException{
        try {
            return client.get(policy, key);
        } catch (AerospikeException e) {
            throw new KeyNotDefinedException();
        }
    }

    public void put(Key key,Bin...bin) throws KeyNotDefinedException{
        try {
            client.put(policy,key,bin);
        } catch (AerospikeException e) {
            throw new KeyNotDefinedException();
        }
    }

    public   AerospikeEntity getEntityObject(Class clazz,Record record) throws GetFailedException{
        GenericVOWrapper voWrapper = markerToWrapperMap.get(clazz);
        AerospikeEntity vo = null;
        if (record != null) {
            try {
                vo = (AerospikeEntity) clazz.newInstance();
            } catch (InstantiationException e1) {
                throw new GetFailedException("Failed to instantiate VO", e1);
            } catch (IllegalAccessException e1) {
                throw new GetFailedException("Failed to instantiate VO", e1);
            }
            for (Map.Entry<String, Method> e : voWrapper.getBinNameToSetterMap().entrySet()) {
                try {
                    if (e.getValue().getParameterTypes()[0].equals(Long.class)) {
                         e.getValue().invoke(vo, record.getLong(e.getKey()));
                    }else if(e.getValue().getParameterTypes()[0].equals(Integer.class)){
                        Object objValue=record.getValue(e.getKey());
                        if(objValue!=null&&!objValue.toString().isEmpty())
                            e.getValue().invoke(vo, Integer.valueOf(objValue.toString()));
                        else
                            e.getValue().invoke(vo, objValue);
                    }else {
                        e.getValue().invoke(vo, record.getValue(e.getKey()));
                    }
                } catch (Exception e1) {
                    throw new GetFailedException("Failed to invoke setter on VO", e1);
                }
            }
        }
        return vo;
    }
}
