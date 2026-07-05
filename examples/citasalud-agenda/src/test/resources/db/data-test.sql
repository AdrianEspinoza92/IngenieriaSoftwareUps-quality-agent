-- Test data with fixed UUIDs (no ON CONFLICT — H2 uses create-drop so tables are always empty)
INSERT INTO medicos (id, nombre, especialidad) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Dr. Test Médico', 'Medicina General');

INSERT INTO pacientes (id, nombre, numero_whatsapp) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Paciente Test', '+593999000001');

-- franjas: cc01=Cucumber US1, cc02=IntegrationTest, cc03=ocupada, cc04=concurrencia, cc05=WhatsAppTest
INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado, version) VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccc01', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-07-15', '09:00', '09:30', 'DISPONIBLE', 0),
    ('cccccccc-cccc-cccc-cccc-cccccccccc02', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-07-15', '09:30', '10:00', 'DISPONIBLE', 0),
    ('cccccccc-cccc-cccc-cccc-cccccccccc03', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-07-15', '10:00', '10:30', 'OCUPADA',    0),
    ('cccccccc-cccc-cccc-cccc-cccccccccc04', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-07-15', '10:30', '11:00', 'DISPONIBLE', 0),
    ('cccccccc-cccc-cccc-cccc-cccccccccc05', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-07-15', '11:00', '11:30', 'DISPONIBLE', 0);
