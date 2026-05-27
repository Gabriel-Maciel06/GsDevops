-- Scripts PL/SQL para Lógicas de Negócio e Relatórios Analíticos no Oracle

ALTER SESSION SET CONTAINER = FREEPDB1;
ALTER SESSION SET CURRENT_SCHEMA = RM562795;

-- Habilitar a saída do DBMS_OUTPUT para fins de depuração
SET SERVEROUTPUT ON;

--------------------------------------------------------------------------------
-- 1. PROCEDIMENTO COM CURSOR EXPLÍCITO (Relatório de Risco)
-- Percorre propriedades com umidade abaixo de 20% e insere um alerta na TB_ALERTA
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROC_RELATORIO_RISCO AS
    -- Cursor explícito para buscar propriedades com leituras de umidade críticas
    CURSOR cur_propriedades_criticas IS
        SELECT p.id_propriedade, p.nome AS nome_propriedade, s.id_sensor, l.valor AS umidade_atual
        FROM TB_PROPRIEDADE p
        JOIN TB_SENSOR s ON p.id_propriedade = s.id_propriedade
        JOIN TB_LEITURA l ON s.id_sensor = l.id_sensor
        WHERE s.tipo_sensor = 'UMIDADE'
          AND l.data_leitura = (
              SELECT MAX(data_leitura)
              FROM TB_LEITURA
              WHERE id_sensor = s.id_sensor
          )
          AND l.valor < 20.00;

    v_id_alerta NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Iniciando Execução do Relatório de Risco ---');

    -- Loop pelo cursor explícito
    FOR reg IN cur_propriedades_criticas LOOP
        DBMS_OUTPUT.PUT_LINE('Propriedade Crítica Detectada: ' || reg.nome_propriedade || 
                             ' | Sensor ID: ' || reg.id_sensor || 
                             ' | Umidade Atual: ' || reg.umidade_atual || '%');

        -- Obter próximo ID da sequência para o alerta
        SELECT SEQ_ALERTA.NEXTVAL INTO v_id_alerta FROM DUAL;

        -- Inserir alerta na tabela
        INSERT INTO TB_ALERTA (id_alerta, mensagem, data_alerta, id_propriedade)
        VALUES (
            v_id_alerta,
            'CRÍTICO: Umidade muito baixa detectada pelo sensor ' || reg.id_sensor || ' (' || reg.umidade_atual || '%). Risco de estresse hídrico!',
            SYSTIMESTAMP,
            reg.id_propriedade
        );
    END LOOP;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('--- Fim da Execução do Relatório de Risco ---');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Erro na execução do relatório de risco: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- 2. PROCEDIMENTO COM DECISÃO E LOOP (Média das 24 horas por área)
-- Calcula a média das últimas 24 horas por propriedade e dispara alerta se for baixa
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROC_CALCULAR_MEDIA_24H AS
    -- Cursor para listar todas as propriedades
    CURSOR cur_propriedades IS
        SELECT id_propriedade, nome FROM TB_PROPRIEDADE;

    v_media NUMBER(10, 2);
    v_id_alerta NUMBER;
    v_limiar_critico CONSTANT NUMBER := 40.00; -- Limiar de 40% de umidade média nas últimas 24h
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Iniciando Análise de Média de Umidade (Últimas 24h) ---');

    FOR prop IN cur_propriedades LOOP
        -- Calcular a média das últimas 24h para os sensores de umidade dessa propriedade
        SELECT AVG(l.valor)
        INTO v_media
        FROM TB_LEITURA l
        JOIN TB_SENSOR s ON l.id_sensor = s.id_sensor
        WHERE s.id_propriedade = prop.id_propriedade
          AND s.tipo_sensor = 'UMIDADE'
          AND l.data_leitura >= (SYSTIMESTAMP - INTERVAL '1' DAY);

        -- Estrutura de decisão para verificar se a média foi calculada e se está abaixo do limite
        IF v_media IS NOT NULL THEN
            DBMS_OUTPUT.PUT_LINE('Propriedade: ' || prop.nome || ' | Umidade Média 24h: ' || v_media || '%');

            IF v_media < v_limiar_critico THEN
                DBMS_OUTPUT.PUT_LINE('Alerta! Umidade média abaixo do limiar de segurança para a propriedade: ' || prop.nome);

                SELECT SEQ_ALERTA.NEXTVAL INTO v_id_alerta FROM DUAL;

                INSERT INTO TB_ALERTA (id_alerta, mensagem, data_alerta, id_propriedade)
                VALUES (
                    v_id_alerta,
                    'AVISO: A média de umidade nas últimas 24 horas está baixa (' || ROUND(v_media, 2) || '%). Recomendável irrigação preventiva.',
                    SYSTIMESTAMP,
                    prop.id_propriedade
                );
            END IF;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Propriedade: ' || prop.nome || ' | Nenhuma leitura de umidade registrada nas últimas 24h.');
        END IF;
    END LOOP;

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('--- Fim da Análise de Média de Umidade ---');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Erro na execução do cálculo da média 24h: ' || SQLERRM);
END;
/

--------------------------------------------------------------------------------
-- 3. PROCEDIMENTO COM TRATAMENTO DE EXCEÇÃO (Inserção Segura de Leituras)
-- Realiza o INSERT com tratamento de erros de integridade (FK inexistente, duplicações)
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROC_INSERIR_LEITURA (
    p_id_sensor IN NUMBER,
    p_valor IN NUMBER,
    p_data_leitura IN TIMESTAMP DEFAULT SYSTIMESTAMP
) AS
    -- Mapeamento de exceções específicas do Oracle
    fk_not_found EXCEPTION;
    PRAGMA EXCEPTION_INIT(fk_not_found, -2291); -- ORA-02291: integridade referencial violada (FK não encontrada)

    dup_key EXCEPTION;
    PRAGMA EXCEPTION_INIT(dup_key, -1); -- ORA-00001: restrição de chave única violada (PK duplicada)
BEGIN
    -- Tentativa de inserção direta no histórico bruto
    INSERT INTO TB_LEITURA (id_sensor, data_leitura, valor)
    VALUES (p_id_sensor, p_data_leitura, p_valor);

    DBMS_OUTPUT.PUT_LINE('Leitura do Sensor ' || p_id_sensor || ' inserida com sucesso. Valor: ' || p_valor);
    COMMIT;
EXCEPTION
    WHEN fk_not_found THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('ERRO DE INTEGRIDADE: O Sensor com ID ' || p_id_sensor || ' não existe na tabela TB_SENSOR.');
        -- Levanta uma exceção amigável de aplicação
        RAISE_APPLICATION_ERROR(-20001, 'Falha ao inserir leitura: O sensor informado não existe.');

    WHEN dup_key THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('ERRO DE CHAVE DUPLICADA: Já existe uma leitura registrada para o sensor ' || p_id_sensor || ' neste exato timestamp.');
        RAISE_APPLICATION_ERROR(-20002, 'Falha ao inserir leitura: Registro duplicado no mesmo timestamp.');

    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('ERRO GENÉRICO: Falha inesperada ao registrar leitura do sensor. Código: ' || SQLCODE || ' | Mensagem: ' || SQLERRM);
        RAISE;
END;
/

--------------------------------------------------------------------------------
-- 4. RELATÓRIOS ANALÍTICOS (JOINS & VIEWS)
--------------------------------------------------------------------------------

-- Relatório A: Performance por Propriedade (JOIN de Usuario + Propriedade + Leitura)
-- Exibe dados consolidados de leitura (Média, Mínima e Máxima) por propriedade e dono
CREATE OR REPLACE VIEW VW_PERFORMANCE_PROPRIEDADE AS
SELECT 
    u.nome AS proprietario,
    u.email AS contato,
    p.nome AS propriedade,
    p.localizacao,
    p.tamanho AS tamanho_hectares,
    s.tipo_sensor,
    COUNT(l.valor) AS total_leituras,
    ROUND(AVG(l.valor), 2) AS valor_medio,
    MIN(l.valor) AS valor_minimo,
    MAX(l.valor) AS valor_maximo
FROM TB_USUARIO u
JOIN TB_PROPRIEDADE p ON u.id_usuario = p.id_usuario
JOIN TB_SENSOR s ON p.id_propriedade = s.id_propriedade
LEFT JOIN TB_LEITURA l ON s.id_sensor = l.id_sensor
GROUP BY u.nome, u.email, p.nome, p.localizacao, p.tamanho, s.tipo_sensor;

-- Relatório B: Sensores sem leitura recente (Sensor com Leitura via LEFT JOIN)
-- Identifica sensores que estão inativos ou não enviam leituras há mais de 1 hora
CREATE OR REPLACE VIEW VW_SENSORES_SEM_LEITURA_RECENTE AS
SELECT 
    s.id_sensor,
    s.tipo_sensor,
    s.modelo,
    s.status AS status_cadastro,
    p.nome AS propriedade,
    MAX(l.data_leitura) AS ultima_leitura,
    CASE 
        WHEN MAX(l.data_leitura) IS NULL THEN 'Sem leituras registradas'
        ELSE 'Inativo/Sem leitura recente'
    END AS status_atividade
FROM TB_SENSOR s
JOIN TB_PROPRIEDADE p ON s.id_propriedade = p.id_propriedade
LEFT JOIN TB_LEITURA l ON s.id_sensor = l.id_sensor
GROUP BY s.id_sensor, s.tipo_sensor, s.modelo, s.status, p.nome
HAVING MAX(l.data_leitura) IS NULL 
    OR MAX(l.data_leitura) < (SYSTIMESTAMP - INTERVAL '1' HOUR);
