package com.uth.biblioteca.views.libros;

import com.uth.biblioteca.data.Libro;
import com.uth.biblioteca.services.SampleBookService;
import com.uth.biblioteca.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.icon.Icon;

@PageTitle("Libros de la Biblioteca")
@Route(value = "/:sampleBookID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class LibrosView extends Div implements BeforeEnterObserver {

    private final String SAMPLEBOOK_ID = "sampleBookID";
    private final String SAMPLEBOOK_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<Libro> grid = new Grid<>(Libro.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField name;
    private TextField author;
    private DatePicker publicationDate;
    private TextField pages;
    private TextField isbn;
    private TextField editorial;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");

    private final BeanValidationBinder<Libro> binder;

    private Libro sampleBook;

    private final SampleBookService sampleBookService;

    public LibrosView(SampleBookService sampleBookService) {
        this.sampleBookService = sampleBookService;
        addClassNames("libros-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        LitRenderer<Libro> imageRenderer = LitRenderer
                .<Libro>of("<img style='height: 64px' src=${item.image} />").withProperty("image", item -> {
                    if (item != null && item.getImage() != null) {
                        return "data:image;base64," + Base64.getEncoder().encodeToString(item.getImage());
                    } else {
                        return "";
                    }
                });
        grid.addColumn(imageRenderer).setHeader("Portada").setWidth("68px").setFlexGrow(0);

        grid.addColumn("name").setAutoWidth(true).setHeader("Nombre");
        grid.addColumn("author").setAutoWidth(true).setHeader("Autor");
        grid.addColumn("editorial").setAutoWidth(true).setHeader("Editorial");
        grid.addColumn("publicationDate").setAutoWidth(true).setHeader("Fecha de Publicación");
        grid.addColumn("pages").setAutoWidth(true).setHeader("Pags.");
        grid.addColumn("isbn").setAutoWidth(true).setHeader("ISBN");
        grid.setItems(query -> sampleBookService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEBOOK_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LibrosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Libro.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(pages).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("pages");

        binder.bindInstanceFields(this);

        attachImageUpload(image, imagePreview);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.setId("btnGuardar");
        save.addClickListener(e -> {
            try {
                if (this.sampleBook == null) {
                    this.sampleBook = new Libro();
                }
                binder.writeBean(this.sampleBook);
                sampleBookService.update(this.sampleBook);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(LibrosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
      
                
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

                Div text = new Div(new Text("El nombre debe de tener entre 3 a 80 caracteres"));

                Button closeButton = new Button(new Icon("lumo", "cross"));
                closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                closeButton.setAriaLabel("Close");
                closeButton.addClickListener(event -> {
                    notification.close();
                });

                HorizontalLayout layout = new HorizontalLayout(text, closeButton);
                layout.setAlignItems(Alignment.CENTER);

                notification.add(layout);
                notification.setPosition(Position.BOTTOM_STRETCH);
                notification.open();
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sampleBookId = event.getRouteParameters().get(SAMPLEBOOK_ID).map(Long::parseLong);
        if (sampleBookId.isPresent()) {
            Optional<Libro> sampleBookFromBackend = sampleBookService.get(sampleBookId.get());
            if (sampleBookFromBackend.isPresent()) {
                populateForm(sampleBookFromBackend.get());
            } else {
                Notification.show(String.format("The requested sampleBook was not found, ID = %s", sampleBookId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LibrosView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        FormLayout formLayout = new FormLayout();
        NativeLabel imageLabel = new NativeLabel("Portada");
        imagePreview = new Image();
        imagePreview.setWidth("100%");
        image = new Upload();
        image.getStyle().set("box-sizing", "border-box");
        image.getElement().appendChild(imagePreview.getElement());
        name = new TextField("Nombre del Libro");
        name.setId("txtNombreLibro");
        name.setPrefixComponent(VaadinIcon.NOTEBOOK.create());
        name.setMinLength(3);
        name.setMaxLength(80);
        name.setErrorMessage("El nombre debe de tener entre 3 a 80 caracteres");
        
        
        author = new TextField("Autor");
        author.setPrefixComponent(VaadinIcon.USER_STAR.create());
        author.setMinLength(5);
        author.setMaxLength(65);
        
        publicationDate = new DatePicker("Fecha de Publicación");
        publicationDate.setHelperText("Fecha de inicio de ventas del libro");
        publicationDate.setMax(now.plusDays(1));
        
        pages = new TextField("Cantidad de Páginas");
        pages.setPrefixComponent(VaadinIcon.ABACUS.create());
        
        isbn = new TextField("Isbn");
        isbn.setPrefixComponent(VaadinIcon.BARCODE.create());
        
        editorial = new TextField("Editorial");
        editorial.setClearButtonVisible(true);
        editorial.setPrefixComponent(VaadinIcon.BOOK.create());
        editorial.setHelperText("Nombre de la Casa Editorial");
        
        formLayout.add(imageLabel, image, name, author, editorial, publicationDate, pages, isbn);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void attachImageUpload(Upload upload, Image preview) {
        ByteArrayOutputStream uploadBuffer = new ByteArrayOutputStream();
        upload.setAcceptedFileTypes("image/*");
        upload.setReceiver((fileName, mimeType) -> {
            uploadBuffer.reset();
            return uploadBuffer;
        });
        upload.addSucceededListener(e -> {
            StreamResource resource = new StreamResource(e.getFileName(),
                    () -> new ByteArrayInputStream(uploadBuffer.toByteArray()));
            preview.setSrc(resource);
            preview.setVisible(true);
            if (this.sampleBook == null) {
                this.sampleBook = new Libro();
            }
            this.sampleBook.setImage(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Libro value) {
        this.sampleBook = value;
        binder.readBean(this.sampleBook);
        this.imagePreview.setVisible(value != null);
        if (value == null || value.getImage() == null) {
            this.image.clearFileList();
            this.imagePreview.setSrc("");
        } else {
            this.imagePreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getImage()));
        }

    }
}
