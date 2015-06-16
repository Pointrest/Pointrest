package com.pointrestapp.pointrest.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

public class PuntiAuthenticator extends AbstractAccountAuthenticator {

	//A dummy authenticator we won't use since our service dosen't  require authentication. 
	public PuntiAuthenticator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
