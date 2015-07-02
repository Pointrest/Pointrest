package com.pointrestapp.pointrest.sync;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

import com.pointrestapp.pointrest.models.Categoria;
import com.pointrestapp.pointrest.models.Punto;
import com.pointrestapp.pointrest.models.Sottocategoria;

public interface PointrestService {

	@GET("/categorie")
	void listCategorie(Callback<List<Categoria>> cb);

	@GET("/sottocategorie")
	void listSottocategorie(Callback<List<Sottocategoria>> cb);

	@GET("/pi/filter/{lat}/{lang}/{raggio}")
	void listPunti(@Path("lat") double lat, @Path("lang") double lang,
			@Path("raggio") double raggio, Callback<List<Punto>> cb);

}
