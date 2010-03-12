package net.deuce.moman.entity.service.rule.impl;

import net.deuce.moman.entity.model.rule.Rule;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;
import net.deuce.moman.entity.service.rule.TransactionRuleService;

import org.springframework.stereotype.Component;

@Component("transactionRuleService")
public class TransactionRuleServiceImpl extends EntityServiceImpl<Rule>
implements TransactionRuleService {

}
