package br.com.timex.evento.programavel.telahistorico;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class TelaHistoricoParceiro {
	public void lancarHistoricoParceiro(BigDecimal codparc, BigDecimal codVendAntes, BigDecimal codVendDepois, Timestamp dataAntesAlteracao, Timestamp dataDepoisAlteracao, BigDecimal usuarioLogadoID) throws MGEModelException {
		// TODO Auto-generated method stub
		
		JapeSession.SessionHandle hnd = null;
		JapeWrapper hisDAO = JapeFactory.dao("AD_HISTPARC");
//		DynamicEntityNames
		try {
			
			hnd = JapeSession.open(); // Abertura da sessão do JapeSession
			DynamicVO histoVo = hisDAO.create()
				.set("CODPARC", codparc)
				.set("CODVEND", codVendAntes)
				.set("CODVENDEP", codVendDepois)
				.set("DTALTER", dataAntesAlteracao)
				.set("DTALTERDEP", dataDepoisAlteracao)
				.set("CODUSU", usuarioLogadoID)
				.save();  	
			
		} catch (Exception e) {
			MGEModelException.throwMe(e);
		} finally {
			JapeSession.close(hnd);
		}
		
	}

	public void lancarHistoricoParceiro2(BigDecimal codparc, BigDecimal codVendAntes, BigDecimal codVendDepois,
	        Timestamp dataAntesAlteracao, Timestamp dataDepoisAlteracao, BigDecimal usuarioLogadoID) throws Exception {
	    
	    final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
	    final JdbcWrapper jdbc = entityFacade.getJdbcWrapper();
	    PreparedStatement pstmt = null;
	    PreparedStatement seqPstmt = null;
	    ResultSet rs = null;
	    BigDecimal codHist = null;
	    
	    // Opção 1: Tentar usar a sequência original
	    String sqlSeq = "SELECT TESTE.SEQ_AD_HISTPARC.NEXTVAL FROM DUAL";
	    
	    // Opção 2: Alternativa usando MAX + 1 (menos eficiente mas funciona)
	    String sqlSeqAlternativa = "SELECT NVL(MAX(CODHIST), 0) + 1 FROM AD_HISTPARC";
	    
	    String sqlInsert = "INSERT INTO AD_HISTPARC (CODHIST,CODPARC, CODVEND, CODVENDEP, DTALTER, DTALTERDEP, CODUSU) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    jdbc.openSession();

	    try {
	        // Primeiro tenta usar a sequência
	        try {
	            seqPstmt = jdbc.getPreparedStatement(sqlSeq);
	            rs = seqPstmt.executeQuery();
	            if (rs.next()) {
	                codHist = rs.getBigDecimal(1);
	            }
	        } catch (Exception seqException) {
	            // Se falhar, usa a alternativa MAX + 1
	            System.out.println("Sequência não encontrada, usando MAX + 1: " + seqException.getMessage());
	            
	            if (rs != null) rs.close();
	            if (seqPstmt != null) seqPstmt.close();
	            
	            seqPstmt = jdbc.getPreparedStatement(sqlSeqAlternativa);
	            rs = seqPstmt.executeQuery();
	            if (rs.next()) {
	                codHist = rs.getBigDecimal(1);
	            }
	        }

	        if (codHist == null) {
	            throw new Exception("Não foi possível obter o próximo valor para CODHIST.");
	        }

	        pstmt = jdbc.getPreparedStatement(sqlInsert);
	        pstmt.setBigDecimal(1, codHist);
	        pstmt.setBigDecimal(2, codparc);
	        pstmt.setBigDecimal(3, codVendAntes);
	        pstmt.setBigDecimal(4, codVendDepois);
	        pstmt.setTimestamp(5, dataAntesAlteracao);
	        pstmt.setTimestamp(6, dataDepoisAlteracao);
	        pstmt.setBigDecimal(7, usuarioLogadoID);

	        pstmt.executeUpdate();
	        
	    } catch (Exception e) {
	        throw new Exception("Erro ao inserir histórico de parceiro: " + e.getMessage(), e);
	    } finally {
	        // Fechar ResultSet
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        // Fechar PreparedStatements
	        if (seqPstmt != null) {
	            try {
	                seqPstmt.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        // Fechar sessão
	        if (jdbc != null) {
	            try {
	                jdbc.closeSession();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}


	
}
//	private void deleteID(BigDecimal codigo) throws MGEModelException {
//		// TODO Auto-generated method stub
//		SessionHandle hnd = null;
//	
//
//    	  try {
//    		  hnd = JapeSession.open();
//    		  JapeWrapper deletDao = JapeFactory.dao("AD_ADSUMARIORDASH");
//    		  deletDao.deleteByCriteria("ID = ? ", codigo);
//    	  } catch (Exception e) {
//				MGEModelException.throwMe(e);
//			} finally {
//				JapeSession.close(hnd);
//			}
//    	   
//    	  
//      }
	
	
	
	

