package br.com.faguiar.brasileiraorestapi.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


import br.com.faguiar.brasileiraorestapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	private static String CASA = "Casa";
	private static String VISITANTE = "Visitante";
	
	public static final String DIV_PENALIDADES = "div[class=imso_mh_s__psn-sc]";
	public static final String ITEM_GOL = "div[class=imso_gs__gs-r]";
	public static final String DIV_GOLS_EQUIPE_CASA =  "div[class=imso_gs__tgs imso_gs__left-team]";
	public static final String DIV_GOLS_EQUIPE_VISITANTE =  "div[class=imso_gs__tgs imso_gs__right-team]";
	public static final String DIV_PLACAR_EQUIPE_CASA = "div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]";
	public static final String DIV_PLACAR_EQUIPE_VISITANTE = "div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]";
	public static final String DIV_LOGO_CASA="div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	public static final String DIV_LOGO_VISITANTE="div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";
	public static final String ITEM_LOGO = "img[class=imso_btl__mh-logo]";
	public static final String DIV_NOME_EQUIPE_CASA = "div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]";
	public static final String DIV_NOME_EQUIPE_VISITANTE = "div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]";
	
	
	
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
			String timeCasa = obterNomeEquipe(document, CASA);
			String timeVisitante = obterNomeEquipe(document, VISITANTE);
			String logoCasa = obterLogoEquipe(document, CASA);
			String logoVisitante = obterLogoEquipe(document, VISITANTE);
			Integer placarEquipeCasa = obterPlacarTime(document, CASA);
			Integer placarEquipeVisitante = obterPlacarTime(document, VISITANTE);
			String golsEquipeCasa = obterGolsEquipe(document, CASA);
			String golsEquipeVisitante = obterGolsEquipe(document, VISITANTE);
			Integer penalidadesCasa =  obterPenalidades(document, CASA);
			LOGGER.info("penalidadesCasa: {}", penalidadesCasa);
			Integer penalidadesVistante =  obterPenalidades(document, VISITANTE);
			LOGGER.info("penalidadesVistante: {}", penalidadesVistante);
			
			
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
	
	public String obterNomeEquipe(Document document, String tipoEquipe) {
		String equipeCasa = null;
		String div = "";
		if (tipoEquipe.equals(CASA)) {
			div = DIV_NOME_EQUIPE_CASA;
		}else {
			div = DIV_NOME_EQUIPE_VISITANTE;
		}
		Element element = document.selectFirst(div);
		equipeCasa = element.select("span").text();
		LOGGER.info("time da casa {} : {}", tipoEquipe, equipeCasa);
		return equipeCasa;
	}
	

	
	public String obterLogoEquipe(Document document, String tipoEquipe) {
		String logo = null;
		String div = "";
		if (tipoEquipe.equals(CASA)) {
			div = DIV_LOGO_CASA;
		}else {
			div = DIV_LOGO_VISITANTE;
		}
		Element element = document.selectFirst(div);

		logo = element.select(ITEM_LOGO).attr("src");
		
		LOGGER.info("2.Logo equipe : {}", logo);
		return logo;
	}

	
	public Integer obterPlacarTime(Document document,String tipoEquipe) {
		Integer placar = null;
		String div = "";
		if (tipoEquipe.equals(CASA)) {
			div = DIV_PLACAR_EQUIPE_CASA;
		}else {
			div = DIV_PLACAR_EQUIPE_VISITANTE;
		}
		String placarEquipe = document.selectFirst(div).text();
		placar = Integer.valueOf(placarEquipe);
		LOGGER.info("3.obterPlacarTime : {}", placar);
		return placar;
		
	}
	
	
	
	public String obterGolsEquipe(Document document, String tipoEquipe) {
		String jogador = null;
		List<String> golsEquipe = new ArrayList<>();
		String div = "";
		if (tipoEquipe.equals(CASA)) {
			div = DIV_GOLS_EQUIPE_CASA;
		}else {
			div = DIV_GOLS_EQUIPE_VISITANTE;
		}
		Elements elementos = document.select(div).select(ITEM_GOL);
		for(Element e: elementos) {
			String infoGol = e.select(ITEM_GOL).text();
			golsEquipe.add(infoGol);
		}
		jogador = String.join(", ", golsEquipe);
		LOGGER.info("4.obterGolsEquipe {} : {}", tipoEquipe, jogador);
		return jogador;
	}
	
	
	
	public Integer obterPenalidades(Document document, String tipoEquipe) {
		String[] divisao = null;
		boolean isPenalidades = document.select(DIV_PENALIDADES).isEmpty();
		if (!isPenalidades) {
			String penalidades = document.select(DIV_PENALIDADES).text();
			String penalidadesCompleta = penalidades.substring(0,5).replace(" ", "");
			divisao = penalidadesCompleta.split("-");
			LOGGER.info("5.obterPenalidades : {}", penalidadesCompleta);
			return tipoEquipe.equals(CASA) ? formataPlacarInteger(divisao[0]) : formataPlacarInteger(divisao[1]);
			
			
		}
		
		return null;
	}
	
	public Integer formataPlacarInteger(String placar) {
		Integer valor ;
		try {
			valor = Integer.parseInt(placar);
		}catch (Exception e) {
			valor = 0;
		}
		return valor;
	}
}
