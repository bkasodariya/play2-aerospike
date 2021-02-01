package model.service;

import model.core.LoadAerospikeConfigRequest;
import model.exception.*;

public interface IAerospikeService {
	void put(AerospikeEntity vo) throws PutFailedException;

	AerospikeEntity get(String key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Long key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Integer key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Float key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Byte key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Double key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Boolean key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	AerospikeEntity get(Character key, Class<? extends AerospikeEntity> clazz) throws GetFailedException;

	boolean remove(String key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Long key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Integer key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Float key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Byte key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Double key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Boolean key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	boolean remove(Character key, Class<? extends AerospikeEntity> clazz) throws RemoveFailedException;

	/**
	 * Loads the context. This should be called at startup.
	 * 
	 * @throws model.exception.LoadFailedException
	 */
	void loadAerospikeConfig(LoadAerospikeConfigRequest request) throws LoadFailedException;
}
