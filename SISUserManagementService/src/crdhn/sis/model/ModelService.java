/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.model;

import java.io.Serializable;
import java.util.List;


public abstract class ModelService {

	public ModelService() {

	}
	public Class getEntityClassByName(String entityName){
		throw new UnsupportedOperationException("Unsupported");
	};
	
	public abstract List list(Class clazz, Class modelClass);

	public abstract Object get(Class clazz, Serializable id,Class modelClass) throws Exception ;

	public abstract boolean delete(Object obj, Class clazz);

	public abstract boolean update(Object obj, Class clazz);
	public abstract Integer create(Object obj, Class clazz);
}
