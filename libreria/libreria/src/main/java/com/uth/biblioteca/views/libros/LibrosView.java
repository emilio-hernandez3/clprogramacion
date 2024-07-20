package com.uth.biblioteca.views.libros;

import com.uth.biblioteca.controller.LibrosInteractor;
import com.uth.biblioteca.controller.LibrosInteractorImpl;
import com.uth.biblioteca.data.Libro;
import com.uth.biblioteca.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.data.provider.DataKeyMapper;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.Rendering;
import com.vaadin.flow.dom.Element;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.icon.Icon;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import com.uth.biblioteca.data.Autor;
import java.util.ArrayList;

@PageTitle("Libros de la Biblioteca")
@Route(value = "/:isbn?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class LibrosView extends Div implements BeforeEnterObserver, LibrosViewModel {

	//@Route(value = "/:sampleBookID?/:action?(edit)", layout = MainLayout.class)
	//private final String SAMPLEBOOK_ID = "sampleBookID";
    private final String SAMPLEBOOK_ID = "isbn";
    private final String SAMPLEBOOK_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<Libro> grid = new Grid<>(Libro.class, false);

    private Upload image;
    private Image imagePreview;
    private TextField name;
    private ComboBox<Autor> autor;
    private DatePicker publicationDate;
    private TextField pages;
    private TextField isbn;
    private TextField editorial;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");

    private Libro libroSeleccionado;
    private List<Libro> elementos;
    private List<Autor> autoresObtenidos;
    private LibrosInteractor controller;

    public LibrosView() {
        addClassNames("libros-view");
        
        elementos = new ArrayList<>();
        autoresObtenidos = new ArrayList<>();
        
        controller = new LibrosInteractorImpl(this);

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
 
		//grid.addColumn("publicationdate").setAutoWidth(true).setHeader("Fecha de Publicación");
		grid.addColumn(new LocalDateRenderer<>(LibrosView::getBookDate,"dd-MM-yyyy"))
			.setHeader("Fecha de Publicación").setSortable(true)
             .setComparator(Libro::getPublicationdate);
		
        grid.addColumn("pages").setAutoWidth(true).setHeader("Pags.");
        grid.addColumn("isbn").setAutoWidth(true).setHeader("ISBN");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEBOOK_EDIT_ROUTE_TEMPLATE, event.getValue().getIsbn()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LibrosView.class);
            }
        });
        
        //ESTO CARGA LOS LIBROS EN PANTALLA
        controller.consultarLibros();
        controller.consultarAutores();

        attachImageUpload(image, imagePreview);

        cancel.setId("btnCancelar");
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.setId("btnGuardar");
        save.addClickListener(e -> {
            try {
                if (this.libroSeleccionado == null) {
                	//SI ENTRA AQUI ESTOY CREANDO UN NUEVO REGISTRO
                	
                    this.libroSeleccionado = new Libro();
                    this.libroSeleccionado.setAuthor(autor.getValue().getNombre());
                    this.libroSeleccionado.setEditorial(editorial.getValue());
                    this.libroSeleccionado.setIsbn(isbn.getValue());
                    this.libroSeleccionado.setName(name.getValue());
                    this.libroSeleccionado.setPages(Integer.valueOf(pages.getValue()));
                    
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date fechaPub = Date.from(publicationDate.getValue().atStartOfDay(defaultZoneId).toInstant());
                    this.libroSeleccionado.setPublicationdate(fechaPub);
                    
                    controller.crearLibro(libroSeleccionado);
                }else {
                	
                	//SI ENTRA AQUI ESTOY MODIFICANDO UN LIBRO EXISTENTE
                    this.libroSeleccionado.setAuthor(autor.getValue().getNombre());
                    this.libroSeleccionado.setEditorial(editorial.getValue());
                    
                    this.libroSeleccionado.setName(name.getValue());
                    this.libroSeleccionado.setPages(Integer.valueOf(pages.getValue()));
                    
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    Date fechaPub = Date.from(publicationDate.getValue().atStartOfDay(defaultZoneId).toInstant());
                    this.libroSeleccionado.setPublicationdate(fechaPub);
                    
                    controller.actualizarLibro(libroSeleccionado);
                	
                }
                clearForm();
                refreshGrid();
                UI.getCurrent().navigate(LibrosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }
    
    private static LocalDate getBookDate(Libro libro) {
        return libro.getPublicationdate().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> isbn = event.getRouteParameters().get(SAMPLEBOOK_ID);
        if (isbn.isPresent()) {
        	Libro seleccionado = buscarLibroPorIsbn(isbn.get());

            if (seleccionado != null) {
                populateForm(seleccionado);
            } else {
                Notification.show(String.format("El libro con ISBN = %s no existe", isbn.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LibrosView.class);
           }
        }
    }

    private Libro buscarLibroPorIsbn(String isbn) {
		Libro encontrado = null;
		for (Libro libro : elementos) {
			if(libro.getIsbn().equals(isbn)) {
				encontrado = libro;
				break;
			}
		}
		return encontrado;
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
        image.setId("uploadPortada");
        image.getStyle().set("box-sizing", "border-box");
        image.getElement().appendChild(imagePreview.getElement());
        name = new TextField("Nombre del Libro");
        name.setId("txtNombreLibro");
        name.setPrefixComponent(VaadinIcon.NOTEBOOK.create());
        name.setMinLength(3);
        name.setMaxLength(80);
        name.setErrorMessage("El nombre debe de tener entre 3 a 80 caracteres");
        
        autor = new ComboBox<>("Autor");
        //autor.setItems(filter, DataService.getPeople());
        autor.setItemLabelGenerator(
                autor -> autor.getNombre());
        autor.setRenderer(createRenderer());
        autor.getStyle().set("--vaadin-combo-box-overlay-width", "16em");
        autor.setId("cbAutorLibro");
        
        publicationDate = new DatePicker("Fecha de Publicación");
        publicationDate.setId("publicationDatePicker");
        publicationDate.setHelperText("Fecha de inicio de ventas del libro");
        publicationDate.setMax(now.plusDays(1));
        
        pages = new TextField("Cantidad de Páginas");
        pages.setId("txtCantidadPaginas");
        pages.setPrefixComponent(VaadinIcon.ABACUS.create());
        
        isbn = new TextField("Isbn");
        isbn.setId("txtIsbn");
        isbn.setPrefixComponent(VaadinIcon.BARCODE.create());
        
        editorial = new TextField("Editorial");
        editorial.setId("txtEditorial");
        editorial.setClearButtonVisible(true);
        editorial.setPrefixComponent(VaadinIcon.BOOK.create());
        editorial.setHelperText("Nombre de la Casa Editorial");
        
        formLayout.add(imageLabel, image, name, autor, editorial, publicationDate, pages, isbn);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }
	
	private Renderer<Autor> createRenderer() {
	    StringBuilder tpl = new StringBuilder();
	    tpl.append("<div style=\"display: flex;\">");
	    tpl.append(
	            "  <img style=\"height: var(--lumo-size-m); margin-right: var(--lumo-space-s);\" src=\"https://randomuser.me/api/portraits/lego/1.jpg\" alt=\"Portrait of ${item.nombre}\" />");
	    tpl.append("  <div>");
	    tpl.append("    ${item.nombre}");
	    tpl.append(
	            "    <div style=\"font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);\">${item.nacimiento}</div>");
	    tpl.append("  </div>");
	    tpl.append("</div>");

	    return LitRenderer.<Autor> of(tpl.toString())
	            .withProperty("nombre", Autor::getNombre)
	            .withProperty("nacimiento", Autor::getNacimiento);
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
            if (this.libroSeleccionado == null) {
                this.libroSeleccionado = new Libro();
            }
            this.libroSeleccionado.setImage(uploadBuffer.toByteArray());
        });
        preview.setVisible(false);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        controller.consultarLibros();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Libro value) {
        this.libroSeleccionado = value;
        
        if(value != null) {
        	Autor autor = buscarAutores(value.getAuthor());
	        this.autor.setValue(autor);
	        this.name.setValue(value.getName());
	        this.pages.setValue(String.valueOf(value.getPages()));
	        this.isbn.setValue(value.getIsbn());
	        this.isbn.setReadOnly(true);
	        this.editorial.setValue(value.getEditorial());
	        this.publicationDate.setValue(getBookDate(value));
	       
	        this.imagePreview.setVisible(value != null);
	        if (value.getImage() == null) {
	            this.image.clearFileList();
	            this.imagePreview.setSrc("");
	        } else {
	            this.imagePreview.setSrc("data:image;base64," + Base64.getEncoder().encodeToString(value.getImage()));
	        }
        }else {
        	this.autor.clear();
	        this.name.setValue("");
	        this.pages.setValue("");
	        this.isbn.setValue("");
	        this.editorial.setValue("");
	        this.publicationDate.setValue(null);
	        
	        this.image.clearFileList();
            this.imagePreview.setSrc("");
        }

    }

	private Autor buscarAutores(String author) {
		Autor obtenido = null;
		for (Autor autor : autoresObtenidos) {
			if(autor.getNombre().equalsIgnoreCase(author)) {
				obtenido = autor;
				break;
			}
		}
		return obtenido;
	}

	@Override
	public void mostrarLibrosEnGrid(List<Libro> items) {
		Collection<Libro> itemsCollection = items;
		grid.setItems(itemsCollection);
		this.elementos = items;
	}

	@Override
	public void mostrarMensajeExito(String mensaje) {
		Notification.show(mensaje);
	}

	@Override
	public void mostrarMensajeError(String mensaje) {
		Notification.show(mensaje);
	}
	
	public void showMessage(String message) {
		 Notification notification = new Notification();
         notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

         Div text = new Div(new Text(message));

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

	@Override
	public void mostrarAutoresEnCombobox(List<Autor> items) {
		Collection<Autor> itemsCollection = items;
		autor.setItems(itemsCollection);
		this.autoresObtenidos = items;
	}
}
