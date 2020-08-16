package com.example.cfra;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.example.model.Security;

@Service
public class XMLService {

	public void parseSecurity(File [] files, Map<String, Security> securities) {

		try {
			var factory = DocumentBuilderFactory.newInstance();
		
			var builder = factory.newDocumentBuilder();
			
			for (File file : files) {
				
				if (!file.isFile() || !file.getName().endsWith(".xml")) {
					continue;
				}
				
		        var doc = builder.parse(file.getAbsoluteFile());
	
		        // normalize XML response
		        doc.getDocumentElement().normalize();
		        
		        var securityList = doc.getElementsByTagName("Security");
	
		        for (int i = 0; i < securityList.getLength(); i++) {
		        	var securityNode = securityList.item(i);
		        	
		        	if (securityNode.getNodeType() == Node.ELEMENT_NODE) {
	                    var elem = (Element) securityNode;
	                    
	                    var companyName = elem.getElementsByTagName("CompanyName").item(0).getTextContent().trim();
	                    var isin = elem.getElementsByTagName("ISIN").item(0).getTextContent().trim();
	                    var targetPrice = elem.getElementsByTagName("TargetPrice").item(0).getTextContent().trim();
	                    var stockRating = elem.getElementsByTagName("StockRating").item(0).getTextContent().trim();
	                    var stockRatingDate = elem.getElementsByTagName("StockRatingDate").item(0).getTextContent().trim();
	                    var stockRatingWM = elem.getElementsByTagName("StockRatingWM").item(0).getTextContent().trim();
	                    
	                    var analystResearchNote = elem.getElementsByTagName("AnalystResearchNote").item(0).getTextContent().trim();
	                    var highlight = elem.getElementsByTagName("Highlight").item(0).getTextContent().trim();
	                    var investmentRisk = elem.getElementsByTagName("InvestmentRisk").item(0).getTextContent().trim();
	                    var businessSummary = elem.getElementsByTagName("BusinessSummary").item(0).getTextContent().trim();
	                    var subIndustryOutlook = elem.getElementsByTagName("SubIndustryOutlook").item(0).getTextContent().trim();
	                    
	                    var security = new Security();
	                    security.setCompanyName(companyName);
	                    security.setIsin(isin);
	                    security.setTargetPrice(targetPrice);
	                    security.setStockRating(stockRating);
	                    security.setStockRatingDate(stockRatingDate);
	                    security.setStockRatingWM(stockRatingWM);
	                    security.setAnalystNote(analystResearchNote);
	                    security.setHighlight(highlight);
	                    security.setInvestmentRisk(investmentRisk);
	                    security.setBusinessSummary(businessSummary);
	                    security.setSubIndustryOutlook(subIndustryOutlook);
	                    
	                    securities.put(isin, security);
		        	}
		        }
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void compare(Map<String, Security> sourceSecurities, Map<String, Security> destinationSecurities) {

		var keys = sourceSecurities.keySet();
		var output = new StringBuilder();
		var removed = new StringBuilder("NOT GENERATING\n===============\n");
		output.append("Total securities in folder1: ").append(sourceSecurities.size());
		output.append("\nTotal securities in folder2: ").append(destinationSecurities.size()).append("\n\n");
		output.append("DESKNOTE GENERATING\n====================\n");
		
		for (String key : keys) {
			
			var source = sourceSecurities.get(key);
			var destination = destinationSecurities.get(key);
			
			if (destination == null) {
				removed.append(source.getCompanyName()).append(" (").append(source.getIsin()).append(") removed from latest file.\n");
				continue;
			}
			
			var changed = false;
//			var targetPriceChanged = false;
//			var stockRatingChanged = false;
//			var stockRatingDateChanged = false;
//			var stockRatingWMChanged = false;
			var highlightChanged = false;
			var investmentRiskChanged = false;
//			var businessSummaryChanged = false;
//			var subIndustryOutlookChanged = false;
			var analystNoteChanged = false;
			
			if (!source.getTargetPrice().equals(destination.getTargetPrice())) {
//				targetPriceChanged = true;
				changed = true;
			}
			if (!source.getStockRating().equals(destination.getStockRating())) {
//				stockRatingChanged = true;
				changed = true;
			}
			if (!source.getStockRatingDate().equals(destination.getStockRatingDate())) {
//				stockRatingDateChanged = true;
				changed = true;
			}
			if (!source.getStockRatingWM().equals(destination.getStockRatingWM())) {
//				stockRatingWMChanged = true;
				changed = true;
			}
			if (!source.getHighlight().equals(destination.getHighlight())) {
				highlightChanged = true;
				changed = true;
			}
			if (!source.getInvestmentRisk().equals(destination.getInvestmentRisk())) {
				investmentRiskChanged = true;
				changed = true;
			}
			if (!source.getBusinessSummary().equals(destination.getBusinessSummary())) {
//				businessSummaryChanged = true;
				changed = true;
			}
			if (!source.getSubIndustryOutlook().equals(destination.getSubIndustryOutlook())) {
//				subIndustryOutlookChanged = true;
				changed = true;
			}
			if (!source.getAnalystNote().equals(destination.getAnalystNote()) && !highlightChanged && !investmentRiskChanged) {
				analystNoteChanged = true;
				changed = true;
			}

			if (changed) {
				output.append(source.getCompanyName()).append(" (").append(source.getIsin()).append(") modified.");
				
				if (destination.getStockRatingWM().equals("0")) {
					output.append(" Dropped coverage.");
				}
				if (analystNoteChanged) {
					output.append(" Use Analyst Note.");
				}
				output.append("\n");
			}
			
			destinationSecurities.remove(key);
		}
		
		var remainingSecurities = destinationSecurities.values();
		for (Security sec : remainingSecurities) {
			output.append(sec.getCompanyName()).append(" (").append(sec.getIsin()).append(") newly added into latest file.\n");
		}
		
		System.out.println(output);
		System.out.println("\n" + removed);
	}

}
