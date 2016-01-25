package controllers;

import java.util.List;

import controllers.ClientController.IOperatorChangedListener;
import models.IExtendedConfiguration;

public interface IOperatorManager<T extends IExtendedConfiguration> {

	void addOperatorChangedListener(IOperatorChangedListener<T> operatorListener);

	void setOperator(T operator);

	void releaseOperator(T operator);

	List<T> getOperators();
}
