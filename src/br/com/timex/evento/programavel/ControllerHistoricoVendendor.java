package br.com.timex.evento.programavel;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;
import br.com.timex.evento.programavel.telahistorico.TelaHistoricoParceiro;

public class ControllerHistoricoVendendor  implements EventoProgramavelJava {
	TelaHistoricoParceiro historico = new TelaHistoricoParceiro();
	  private Timestamp dataAntesAlteracao;
	  private  BigDecimal codVendAntes;
	  private  String nomeVendAntes;
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
        DynamicVO vo = (DynamicVO) event.getVo();

        // Guarda data antes
        dataAntesAlteracao = vo.asTimestamp("DTALTER");
        BigDecimal codParc = vo.asBigDecimal("CODPARC");
     // Consulta o valor atual (antes da alteração) diretamente do banco
        JapeWrapper parceiroDAO = JapeFactory.dao("Parceiro");
        DynamicVO parceiroVO = parceiroDAO.findByPK(codParc);

        codVendAntes = parceiroVO.asBigDecimal("CODVEND");

        // Busca nome do vendedor anterior
        if (codVendAntes != null) {
            JapeWrapper vendDAO = JapeFactory.dao("Vendedor");
            DynamicVO vendedorAntesVO = vendDAO.findByPK(codVendAntes);
            nomeVendAntes = vendedorAntesVO.asString("APELIDO");
        } else {
            nomeVendAntes = "<nenhum>";
        }
    }
	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		    DynamicVO vo = (DynamicVO) event.getVo();
		     ServiceContext sc = ServiceContext.getCurrent();
			 BigDecimal usuarioLogadoID = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUserID();
			 String usuarioLogadoNome = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication()).getUsuVO().getNOMEUSU();
			 BigDecimal codParc = vo.asBigDecimal("CODPARC"); // Pega o parceiro selecionado
			 
			
		 // Captura o valor depois da alteração
	        Timestamp dataDepoisAlteracao = vo.asTimestamp("DTALTER");
	        // Pega vendedor depois
	        BigDecimal codVendDepois = vo.asBigDecimal("CODVEND");
	        JapeWrapper vendDAO = JapeFactory.dao("Vendedor");
	        DynamicVO vendedorDepoisVO = vendDAO.findByPK(codVendDepois);
	        String nomeVendDepois = vendedorDepoisVO.asString("APELIDO");
	        
	        
            historico.lancarHistoricoParceiro2(codParc,codVendAntes,codVendDepois,dataAntesAlteracao,dataDepoisAlteracao,usuarioLogadoID);
	        // Monta mensagem
//	        String mensagem = "Alteração de parceiro:<br>" +
//	                          "Código do Parceiro: " + codParc + "<br><br>" +
//	                          "<strong>Vendedor ANTES:</strong> " + codVendAntes + " (" + nomeVendAntes + ")<br>" +
//                          "<strong>Vendedor ATUAL:</strong> " + codVendDepois + " (" + nomeVendDepois + ")<br><br>" +
//                          "<strong>Data ANTES:</strong> " + dataAntesAlteracao + "<br>" +
//                          "<strong>Data ATUAL:</strong> " + dataDepoisAlteracao + "<br><br>" +
//	                          "Usuário logado: " + usuarioLogadoID + " (" + usuarioLogadoNome + ")";
//	        AD_HISTPARC
//        sc.setStatusMessage(mensagem);
//        sc.setStatus(2); // Warning
	}



}
