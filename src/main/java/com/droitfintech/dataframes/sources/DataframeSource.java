package com.droitfintech.dataframes.sources;

import com.droitfintech.dataframes.Dataframe;

public interface DataframeSource  {

	public Dataframe load(String name);
}
