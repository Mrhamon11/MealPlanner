package com.mealplanner.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("hello")
public class HelloView extends VerticalLayout
{

    public HelloView()
    {
        TextField textField = new TextField();
        Button submitButton = new Button("Submit");
        H1 greeting = new H1();

        submitButton.addClickListener(event -> {
            String name = textField.getValue();
            if (name != null && !name.isEmpty())
            {
                greeting.setText("Hello " + name);
            }
        });

        HorizontalLayout inputLayout = new HorizontalLayout(textField, submitButton);
        inputLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(inputLayout, greeting);

        // Center the content
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);
    }
}
