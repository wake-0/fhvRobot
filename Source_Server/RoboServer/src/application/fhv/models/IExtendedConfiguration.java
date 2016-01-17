package models;

import communication.configurations.IConfiguration;

public interface IExtendedConfiguration extends IConfiguration {

	public boolean getIsOperator();

	public void setIsOperator(boolean value);

}
