package net.deuce.moman.entity.service.income.impl;

import net.deuce.moman.entity.model.income.Income;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;
import net.deuce.moman.entity.service.income.IncomeService;

import org.springframework.stereotype.Component;

@Component("incomeService")
public class IncomeServiceImpl extends EntityServiceImpl<Income> implements IncomeService {

}
