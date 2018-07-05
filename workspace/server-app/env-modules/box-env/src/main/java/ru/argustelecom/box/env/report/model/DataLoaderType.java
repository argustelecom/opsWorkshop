package ru.argustelecom.box.env.report.model;

import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum DataLoaderType {

	GROOVY(DefaultLoaderFactory.GROOVY_DATA_LOADER), SQL(DefaultLoaderFactory.SQL_DATA_LOADER);

	private String name;

}