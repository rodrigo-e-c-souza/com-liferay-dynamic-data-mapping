/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.io.internal;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.io.DDMFormFieldJSONObjectTransformer;
import com.liferay.dynamic.data.mapping.io.DDMFormJSONDeserializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.test.util.DDMFormFieldTypeSettingsTestUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Marcellus Tavares
 */
public class DDMFormJSONDeserializerTest
	extends BaseDDMFormDeserializerTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpDDMFormJSONDeserializer();
	}

	@Override
	protected DDMForm deserialize(String serializedDDMForm)
		throws PortalException {

		return _ddmFormJSONDeserializer.deserialize(serializedDDMForm);
	}

	protected DDMFormFieldJSONObjectTransformer
		getDDMFormFieldJSONObjectTransformer() {

		DDMFormFieldJSONObjectTransformerImpl
			ddmFormFieldJSONObjectConverterImpl =
				new DDMFormFieldJSONObjectTransformerImpl();

		ddmFormFieldJSONObjectConverterImpl.
			jsonObjectToDDMFormFieldTransformer =
				new JSONObjectToDDMFormFieldTransformer(
					getMockedDDMFormFieldTypeServicesTracker(),
					new JSONFactoryImpl());

		return ddmFormFieldJSONObjectConverterImpl;
	}

	@Override
	protected String getDeserializerType() {
		return "json";
	}

	protected DDMFormFieldTypeServicesTracker
		getMockedDDMFormFieldTypeServicesTracker() {

		setUpDefaultDDMFormFieldType();

		DDMFormFieldTypeServicesTracker ddmFormFieldTypeServicesTracker = mock(
			DDMFormFieldTypeServicesTracker.class);

		DDMFormFieldRenderer ddmFormFieldRenderer = mock(
			DDMFormFieldRenderer.class);

		when(
			ddmFormFieldTypeServicesTracker.getDDMFormFieldRenderer(
				Matchers.anyString())
		).thenReturn(
			ddmFormFieldRenderer
		);

		when(
			ddmFormFieldTypeServicesTracker.getDDMFormFieldType(
				Matchers.anyString())
		).thenReturn(
			_defaultDDMFormFieldType
		);

		Map<String, Object> properties = new HashMap<>();

		properties.put("ddm.form.field.type.icon", "my-icon");
		properties.put(
			"ddm.form.field.type.js.class.name", "myJavaScriptClass");
		properties.put("ddm.form.field.type.js.module", "myJavaScriptModule");

		when(
			ddmFormFieldTypeServicesTracker.getDDMFormFieldTypeProperties(
				Matchers.anyString())
		).thenReturn(
			properties
		);

		return ddmFormFieldTypeServicesTracker;
	}

	@Override
	protected String getTestFileExtension() {
		return ".json";
	}

	protected void setUpDDMFormJSONDeserializer() throws Exception {
		DDMFormJSONDeserializerImpl ddmFormJSONDeserializerImpl =
			new DDMFormJSONDeserializerImpl();

		ddmFormJSONDeserializerImpl.jsonFactory = new JSONFactoryImpl();

		ddmFormJSONDeserializerImpl.ddmFormFieldJSONObjectTransformer =
			getDDMFormFieldJSONObjectTransformer();

		_ddmFormJSONDeserializer = ddmFormJSONDeserializerImpl;
	}

	protected void setUpDefaultDDMFormFieldType() {
		when(
			_defaultDDMFormFieldType.getDDMFormFieldTypeSettings()
		).then(
			new Answer<Class<? extends DDMFormFieldTypeSettings>>() {

				@Override
				public Class<? extends DDMFormFieldTypeSettings> answer(
						InvocationOnMock invocationOnMock)
					throws Throwable {

					return DDMFormFieldTypeSettingsTestUtil.getSettings();
				}

			}
		);
	}

	@Override
	protected void testBooleanDDMFormField(DDMFormField ddmFormField) {
		super.testBooleanDDMFormField(ddmFormField);

		DDMFormFieldValidation ddmFormFieldValidation =
			ddmFormField.getDDMFormFieldValidation();

		Assert.assertNotNull(ddmFormFieldValidation);
		Assert.assertEquals(
			"Boolean2282", ddmFormFieldValidation.getExpression());
		Assert.assertEquals(
			"You must check this box to continue.",
			ddmFormFieldValidation.getErrorMessage());

		Assert.assertEquals("true", ddmFormField.getVisibilityExpression());
	}

	@Override
	protected void testDDMFormRules(List<DDMFormRule> ddmFormRules) {
		Assert.assertEquals(ddmFormRules.toString(), 2, ddmFormRules.size());

		DDMFormRule ddmFormRule1 = ddmFormRules.get(0);

		Assert.assertEquals("Condition 1", ddmFormRule1.getCondition());
		Assert.assertEquals(
			Arrays.asList("Action 1", "Action 2"), ddmFormRule1.getActions());
		Assert.assertTrue(ddmFormRule1.isEnabled());

		DDMFormRule ddmFormRule2 = ddmFormRules.get(1);

		Assert.assertEquals("Condition 2", ddmFormRule2.getCondition());
		Assert.assertEquals(
			Arrays.asList("Action 3"), ddmFormRule2.getActions());
		Assert.assertFalse(ddmFormRule2.isEnabled());
	}

	@Override
	protected void testDDMFormSuccessPageSettings(
		DDMFormSuccessPageSettings ddmFormSuccessPageSettings) {

		Assert.assertNotNull(ddmFormSuccessPageSettings);
		Assert.assertEquals("Body Text", ddmFormSuccessPageSettings.getBody());
		Assert.assertEquals(
			"Title Text", ddmFormSuccessPageSettings.getTitle());
		Assert.assertTrue(ddmFormSuccessPageSettings.isEnabled());
	}

	@Override
	protected void testDecimalDDMFormField(DDMFormField ddmFormField) {
		super.testDecimalDDMFormField(ddmFormField);

		Assert.assertEquals("false", ddmFormField.getVisibilityExpression());
	}

	private DDMFormJSONDeserializer _ddmFormJSONDeserializer;

	@Mock
	private DDMFormFieldType _defaultDDMFormFieldType;

}