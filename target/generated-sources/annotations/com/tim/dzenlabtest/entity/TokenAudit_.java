package com.tim.dzenlabtest.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TokenAudit.class)
public abstract class TokenAudit_ {

	public static volatile SingularAttribute<TokenAudit, Long> id;
	public static volatile SingularAttribute<TokenAudit, User> user;
	public static volatile SingularAttribute<TokenAudit, ApiToken> token;

}

