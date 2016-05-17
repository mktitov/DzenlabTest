package com.tim.dzenlabtest.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ApiToken.class)
public abstract class ApiToken_ {

	public static volatile SingularAttribute<ApiToken, String> token;
	public static volatile SingularAttribute<ApiToken, Date> expirationDate;

}

