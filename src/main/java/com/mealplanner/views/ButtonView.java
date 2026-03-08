package com.mealplanner.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("buttonview")
public class ButtonView extends VerticalLayout
{
    public ButtonView()
    {

        Button button = new Button("Press me!");
        H1 message = new H1();

        button.addClickListener(event -> {
            message.setText("Thank you for pressing me!");
        });


        HorizontalLayout inputLayout = new HorizontalLayout(button);
        inputLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        message.getStyle().set("color", "blue");

        add(button, message);

        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }
}
