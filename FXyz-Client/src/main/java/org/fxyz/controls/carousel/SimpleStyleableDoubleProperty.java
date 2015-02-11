package org.fxyz.controls.carousel;

import java.util.HashMap;
import java.util.Map;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;

public class SimpleStyleableDoubleProperty extends StyleableDoubleProperty {
  private static final Map<Class<?>, Map<String, CssMetaData<? extends Styleable, Number>>> CSS_META_DATA = new HashMap<>();

  private final Object bean;
  private final String name;
  private final String cssName;
  private final double initialValue;

  public SimpleStyleableDoubleProperty(Object bean, String name, String cssName, double initialValue) {
    super(initialValue);

    this.bean = bean;
    this.name = name;
    this.cssName = cssName;
    this.initialValue = initialValue;
  }

  @Override
  public CssMetaData<? extends Styleable, Number> getCssMetaData() {
    Map<String, CssMetaData<?, Number>> cssMetaDataByName = CSS_META_DATA.get(bean.getClass());

    if(cssMetaDataByName == null) {
      cssMetaDataByName = new HashMap<>();
      CSS_META_DATA.put(bean.getClass(), cssMetaDataByName);
    }

    CssMetaData<? extends Styleable, Number> cssMetaData = cssMetaDataByName.get(cssName);

    if(cssMetaData == null) {
      cssMetaData = new CssMetaData<Styleable, Number>(cssName, StyleConverter.getSizeConverter(), initialValue) {
        @Override
        public boolean isSettable(Styleable styleable) {
          return !SimpleStyleableDoubleProperty.this.isBound();
        }

        @Override
        public StyleableDoubleProperty getStyleableProperty(Styleable styleable) {
          return SimpleStyleableDoubleProperty.this;
        }
      };
      cssMetaDataByName.put(cssName, cssMetaData);
    }

    return cssMetaData;
  }

  @Override
  public Object getBean() {
    return bean;
  }

  @Override
  public String getName() {
    return name;
  }
}
