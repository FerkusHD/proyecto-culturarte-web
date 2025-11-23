<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pagar Colaboraciones - Culturarte</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 600px;
            margin: 0 auto;
        }
        
        .header {
            background: white;
            padding: 20px;
            border-radius: 15px 15px 0 0;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .header h1 {
            color: #667eea;
            font-size: 24px;
            margin-bottom: 5px;
        }
        
        .header p {
            color: #666;
            font-size: 14px;
        }
        
        .content {
            background: white;
            padding: 20px;
            border-radius: 0 0 15px 15px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            margin-top: 2px;
        }
        
        .colaboracion-card {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 15px;
            margin-bottom: 15px;
            border-left: 4px solid #667eea;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .colaboracion-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }
        
        .colaboracion-header {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .propuesta-imagen {
            width: 60px;
            height: 60px;
            border-radius: 8px;
            object-fit: cover;
            margin-right: 15px;
        }
        
        .propuesta-info h3 {
            color: #333;
            font-size: 18px;
            margin-bottom: 5px;
        }
        
        .propuesta-info .fecha {
            color: #666;
            font-size: 13px;
        }
        
        .colaboracion-detalles {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 10px;
            padding-top: 10px;
            border-top: 1px solid #ddd;
        }
        
        .monto {
            font-size: 24px;
            font-weight: bold;
            color: #667eea;
        }
        
        .btn-pagar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 25px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-pagar:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
        
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #666;
        }
        
        .empty-state svg {
            width: 80px;
            height: 80px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        .badge-pagado {
            background: #28a745;
            color: white;
            padding: 5px 12px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>💳 Pagar Colaboraciones</h1>
            <p>Selecciona una colaboración para realizar el pago</p>
        </div>
        
        <div class="content">
            <c:choose>
                <c:when test="${empty colaboraciones}">
                    <div class="empty-state">
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <h3>No hay colaboraciones pendientes de pago</h3>
                        <p>Todas tus colaboraciones están al día</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${colaboraciones}" var="colab">
                        <div class="colaboracion-card">
                            <div class="colaboracion-header">
                                <c:choose>
                                    <c:when test="${not empty colab.propuesta.imagen}">
                                        <img src="${pageContext.request.contextPath}/uploads/${colab.propuesta.imagen}" 
                                             alt="${colab.tituloPropuesta}" 
                                             class="propuesta-imagen"
                                             onerror="this.src='${pageContext.request.contextPath}/images/default-propuesta.png'">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="propuesta-imagen" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"></div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <div class="propuesta-info">
                                    <h3>${colab.tituloPropuesta}</h3>
                                    <p class="fecha">
                                        <fmt:formatDate value="${colab.fecha}" pattern="dd/MM/yyyy" /> - 
                                        <fmt:formatDate value="${colab.hora}" pattern="HH:mm" />
                                    </p>
                                </div>
                            </div>
                            
                            <div class="colaboracion-detalles">
                                <div class="monto">$<fmt:formatNumber value="${colab.monto}" pattern="#,##0.00" /></div>
                                <a href="${pageContext.request.contextPath}/colaboraciones/pago/formulario?tituloPropuesta=${colab.tituloPropuesta}" 
                                   class="btn-pagar">
                                    Pagar Ahora
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
