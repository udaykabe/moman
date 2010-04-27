package net.deuce.moman.entity.service.payee.impl;

import net.deuce.moman.entity.model.payee.Payee;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;
import net.deuce.moman.entity.service.payee.PayeeService;

import org.springframework.stereotype.Component;

@Component("payeeService")
public class PayeeServiceImpl extends EntityServiceImpl<Payee> implements PayeeService {

}
