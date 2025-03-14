package ru.eddyz.adminpanel.components;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.server.AbstractStreamResource;

import java.util.Optional;


@Tag("data-binding-view")
@JsModule("./components/Video.ts")
public class Video extends Component {
    private static final String ALT_ATTRIBUTE = "alt";
    private static final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors.attributeWithDefault("src", "");

    public Video() {
    }

    public Video(String src, String alt) {
        this.setSrc(src);
        this.setAlt(alt);
    }

    public Video(AbstractStreamResource src, String alt) {
        this.setSrc(src);
        this.setAlt(alt);
    }

    public String getSrc() {
        return (String)this.get(srcDescriptor);
    }

    public void setSrc(String src) {
        this.set(srcDescriptor, src);
    }

    public void setSrc(AbstractStreamResource src) {
        this.getElement().setAttribute("src", src);
    }

    public void setHeight(String height) {
        this.getElement().setAttribute("height", height);
    }

    public void setWidth(String width) {
        this.getElement().setAttribute("width", width);
    }

    public void setMaxWidth(String maxWidth) {
        this.getElement().setAttribute("maxWidth", maxWidth);
    }

    public void setMaxHeight(String maxHeight) {
        this.getElement().setAttribute("maxHeight", maxHeight);
    }

    public void setAlt(String alt) {
        if (alt == null) {
            this.getElement().removeAttribute("alt");
        } else {
            this.getElement().setAttribute("alt", alt);
        }

    }

    public Optional<String> getAlt() {
        return Optional.ofNullable(this.getElement().getAttribute("alt"));
    }
}
