package br.com.faguiar.brasileiraorestapi.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

import br.com.faguiar.brasileiraorestapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	public static void main(String[] args) {
		String url = BASE_URL_GOOGLE + "fluminense+x+bragantino" + COMPLEMENTO_URL_GOOGLE;
		
		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obterInformacoesPartida(url);
		
		

	}
	
	public PartidaGoogleDTO obterInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		Document document =  null;
		
		try {
			document = Jsoup.connect(url).get();
			String title = document.title();
			LOGGER.info("titulo da pagina: {}", title);
			StatusPartida statusPartida = obterStatusPartida(document);
			if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obterTempoPartida(document);
				
			}
			String timeCasa = obterNomeEquipeCasa(document);
			String timeVisitante = obterNomeEquipeVisitante(document);
			String logoCasa = obterLogoEquipeCasa(document);
			String logoVisitante = obterLogoEquipeVisitante(document);
			
		} catch (IOException e) {
			LOGGER.error("ERRO AO TENTAR CONECTAR NO GOOGLE COM JSOUP : {}", e.getMessage());
		
		}
		
		return partida;
	}
	
	public StatusPartida obterStatusPartida(Document document) {
		
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		
		
		
		if (!isTempoPartida) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
				
			}
			LOGGER.info(tempoPartida);
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		
		if (!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		LOGGER.info("status partida : {}", statusPartida.toString());
		
		return statusPartida;
				
	}
	
	public String obterTempoPartida(Document document) {
		String tempoPartida = null;
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		
		if (!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}
		LOGGER.info("tempo partida : {}", tempoPartida);
		return tempoPartida;
	}
	
	public String obterNomeEquipeCasa(Document document) {
		String equipeCasa = null;
		Element element = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		equipeCasa = element.select("span").text();
		LOGGER.info("time da casa : {}", equipeCasa);
		return equipeCasa;
	}
	
	
	public String obterNomeEquipeVisitante(Document document) {
		String equipeVisitante = null;
		Element element = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		equipeVisitante = element.select("span").text();
		LOGGER.info("time da visitante : {}", equipeVisitante);
		return equipeVisitante;
	}
	
	public String obterLogoEquipeCasa(Document document) {
		String logo = null;
		Element element = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		logo = element.select("img[class=imso_btl__mh-logo]").attr("src");
		LOGGER.info("2.Logo equipe casa : {}", logo);
		return logo;
	}

	public String obterLogoEquipeVisitante(Document document) {
		String logo = null;
		Element element = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		logo = element.select("img[class=imso_btl__mh-logo]").attr("src");
		LOGGER.info("2.Logo equipe visitante : {}", logo);
		System.out.println(element.select("img[class=imso_btl__mh-logo]").hasAttr("src"));
		return logo;
	}
	
	
	//"" currentSrc
}
