package com.schwab;

import org.osgi.service.component.annotations.Reference;

import com.adobe.aemfd.docmanager.Document;
import com.adobe.fd.forms.api.AcrobatVersion;
import com.adobe.fd.forms.api.FormsService;
import com.adobe.fd.forms.api.FormsServiceException;
import com.adobe.fd.forms.api.PDFFormRenderOptions;

public class XDP2PDFF {
	@Reference
	static
	FormsService formsService;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PDFFormRenderOptions renderOptions = new PDFFormRenderOptions();
		renderOptions.setAcrobatVersion(AcrobatVersion.Acrobat_11);
		try {
			Document xdpRenderedAsPDF = formsService.renderPDFForm("D:\\CS\\Forms\\APP\\LD12 - APP13051 - Attorney_in_Fact.xdp", null, renderOptions);
			
			//return xdpRenderedAsPDF;
		} catch (FormsServiceException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
