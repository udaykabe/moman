package net.deuce.moman.entity.service.transaction.impl;

import net.deuce.moman.entity.model.transaction.InternalTransaction;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;
import net.deuce.moman.entity.service.transaction.ImportService;

import org.springframework.stereotype.Component;

@Component("importService")
public class ImportServiceImpl extends EntityServiceImpl<InternalTransaction>
implements ImportService {
	
	
}
