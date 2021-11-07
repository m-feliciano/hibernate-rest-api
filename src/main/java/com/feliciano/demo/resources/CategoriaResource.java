package com.feliciano.demo.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.feliciano.demo.dto.CategoriaDTO;
import com.feliciano.demo.resources.domain.Categoria;
import com.feliciano.demo.services.CategoriaService;
import com.feliciano.demo.services.exceptions.DataIntegrityException;

@RestController
@RequestMapping(value = "/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaService service;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	private ResponseEntity<Categoria> find(@PathVariable Integer id) {
		Categoria obj = service.find(id);
		return ResponseEntity.ok().body(obj);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody Categoria obj) { // @RequestBody converts JSON to object body
		obj = service.insert(obj);
		ServletUriComponentsBuilder.fromCurrentRequest();
		URI uri = ServletUriComponentsBuilder.fromPath("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody Categoria obj, @PathVariable Integer id) {
		obj.setId(id);
		service.update(obj);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		try {
			service.delete(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é posisvel excluir uma categoria que possui produtos!");
		}
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method = RequestMethod.GET)
	private ResponseEntity<List<CategoriaDTO>> find() {
		List<Categoria> list = service.findAll();
		List<CategoriaDTO> listDto = list.stream()
				.map(obj -> new CategoriaDTO(obj))
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

}
