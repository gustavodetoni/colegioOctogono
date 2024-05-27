import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './sistema.css';
import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';

interface Aluno {
  id: number;
  nome: string;
  matricula: string;
  emailResponsavel: string | null;
  turmas: Turma[];
  materias: Materia[];
}

interface Turma {
  id: number;
  nome: string;
  alunos: Aluno[] | null;
  materias: Materia[] | null;
}

interface Materia {
  id: number;
  nome: string;
  professores: null;
  turmas: Turma[] | null;
  aluno: Aluno | null;
}

interface Relatorio {
  nomeAluno: string;
  nomeMateria: string;
  faltas: number;
}

const List: React.FC = () => {
  const [turmas, setTurmas] = useState<Turma[]>([]);
  const [materias, setMaterias] = useState<Materia[]>([]);
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [alunosSelecionados, setAlunosSelecionados] = useState<number[]>([]);
  const [selectedTurma, setSelectedTurma] = useState<number | null>(null);
  const [selectedMateria, setSelectedMateria] = useState<number | null>(null);
  const [confirmacaoAberta, setConfirmacaoAberta] = useState<boolean>(false);
  const [relatorios, setRelatorios] = useState<Relatorio[]>([]);

  useEffect(() => {
    axios.get<Turma[]>('https://colegiooctogono.onrender.com/sistema/turmas')
      .then(response => { 
        setTurmas(response.data);
      })
      .catch(error => {
        console.error('Erro ao buscar turmas:', error);
      });
  }, []);

  useEffect(() => {
    if (selectedTurma !== null) {
      axios.get<Materia[]>(`https://colegiooctogono.onrender.com/sistema/${selectedTurma}/materias`)
        .then(response => {
          setMaterias(response.data);
        })
        .catch(error => {
          console.error('Erro ao buscar matérias:', error);
        });
    } else {
      setMaterias([]);
    }
  }, [selectedTurma]);

  useEffect(() => {
    if (selectedTurma !== null && selectedMateria !== null) {
      axios.get<Aluno[]>(`https://colegiooctogono.onrender.com/sistema/turma/${selectedTurma}/materia/${selectedMateria}/alunos`)
        .then(response => {
          setAlunos(response.data);
        })
        .catch(error => {
          console.error('Erro ao buscar alunos:', error);
        });
    } else {
      setAlunos([]);
    }
  }, [selectedTurma, selectedMateria]);
  

  const gerarRelatorio = () => {
    if (selectedMateria && alunosSelecionados.length > 0) {
      axios.get(`https://colegiooctogono.onrender.com/sistema/relatorio-faltas/alunos/materia/${selectedMateria}`, {
        params: {
          alunoIds: alunosSelecionados.join(',')
        }
      })
      .then(response => {
        setRelatorios(response.data);
      })
      .catch(error => {
        console.error('Erro ao gerar relatórios:', error);
      });
    } else {
      console.error('Selecione uma matéria e pelo menos um aluno para gerar o relatório.');
    }
  };

  const handleTurmaChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedTurma(Number(event.target.value));
    setSelectedMateria(null);
    setAlunosSelecionados([]); 
    setRelatorios([]);
  };
  
  const handleMateriaChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedMateria(Number(event.target.value));
    setAlunosSelecionados([]); 
    setRelatorios([]);
  };

  const handleAlunoCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>, alunoId: number) => {
    const isChecked = event.target.checked;
    if (isChecked) {
      setAlunosSelecionados([...alunosSelecionados, alunoId]);
    } else {
      setAlunosSelecionados(alunosSelecionados.filter(id => id !== alunoId));
    }
  };

  const abrirConfirmacao = () => {
    setConfirmacaoAberta(true);
  };

  const fecharConfirmacao = () => {
    setConfirmacaoAberta(false);
  };

  const confirmarRegistroFaltas = () => {
    registrarFaltas();
    fecharConfirmacao();
    setRelatorios([]);
  };

  const registrarFaltas = () => {
    if (selectedTurma && selectedMateria && alunosSelecionados.length > 0) {
      axios.post(`https://colegiooctogono.onrender.com/sistema/1/materia/${selectedMateria}/turma/${selectedTurma}/registrar-faltas`, alunosSelecionados)
        .then(() => {
          console.log('Faltas registradas com sucesso');
          setAlunosSelecionados([]);
          setSelectedTurma(null);
          setSelectedMateria(null);
        })
        .catch(error => {
          console.error('Erro ao registrar faltas:', error);
        });
    } else {
      console.error('Selecione turma, matéria e pelo menos um aluno para registrar faltas.');
    }
  };

  const enviarEmail = (nomeAluno: string, nomeMateria: string, faltas: number) => {
    const mensagem = `Prezado Responsável,\n\nGostaríamos de informá-lo sobre a frequência escolar do aluno ${nomeAluno}, que está matriculado na matéria ${nomeMateria} neste semestre.\n\nDurante o período recente, observamos que o ${nomeAluno} tem tido uma frequência de presença abaixo do esperado.Com o total de ${faltas} É de extrema importância que os alunos estejam presentes em todas as aulas para garantir um progresso acadêmico consistente e um ambiente de aprendizagem eficaz.\n\nA frequência escolar do ${nomeAluno} é um componente crucial para o seu sucesso acadêmico. A ausência constante pode impactar negativamente o desempenho acadêmico e comprometer o desenvolvimento educacional a longo prazo.\n\nGostaríamos de pedir sua colaboração para ajudar o ${nomeAluno} a melhorar sua frequência escolar. Por favor, revise a situação com ele e forneça o apoio necessário para garantir que ele esteja presente em todas as aulas.\n\nSe você tiver alguma dúvida ou preocupação, não hesite em entrar em contato conosco. Estamos aqui para apoiá-lo e trabalhar juntos para garantir o sucesso acadêmico do ${nomeAluno}.\n\nAgradecemos sua atenção e colaboração.\n\nAtenciosamente,\nColegio Octogono`;
    const blob = new Blob([mensagem], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `relatorio_faltas_${nomeAluno}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div>
      <div className="presenca">
      <select value={selectedTurma !== null ? String(selectedTurma) : ''} onChange={handleTurmaChange}>
        <option value="" disabled hidden>Selecione a turma</option>
        {turmas.map(turma => (
          <option key={turma.id} value={turma.id}>{turma.nome}</option>
        ))}
      </select>

      <select value={selectedMateria !== null ? String(selectedMateria) : ''} onChange={handleMateriaChange} disabled={materias.length === 0}>
        <option value="" disabled hidden>Selecione a matéria</option>
        {materias.map(materia => (
          <option key={materia.id} value={materia.id}>{materia.nome}</option>
        ))}
      </select>
      <div className="registrar">
      <button onClick={abrirConfirmacao}>Registrar Faltas</button>
      <Dialog
          open={confirmacaoAberta}
          onClose={fecharConfirmacao}
        >
          <DialogTitle>Confirmação</DialogTitle>
          <DialogContent>
            <DialogContentText>Deseja realmente registrar as faltas?</DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={fecharConfirmacao} color="primary">
              Cancelar
            </Button>
            <Button onClick={confirmarRegistroFaltas} color="primary" autoFocus>
              Registrar
            </Button>
          </DialogActions>
        </Dialog>
      </div>
      <div className="relatorio">
      <button onClick={gerarRelatorio}>Gerar Relatório</button>
      </div>
      </div>
      
      <ul> 
        {alunos.map(aluno => (
          <li key={aluno.id}>
            <label  className="aluno-checkbox-label">
              <input
                type="checkbox"
                checked={alunosSelecionados.includes(aluno.id)}
                onChange={(e) => handleAlunoCheckboxChange(e, aluno.id)}
              />
              <span className="aluno-nome">{aluno.nome}</span>
            </label>
          </li>
        ))}
      </ul>
      {relatorios.length > 0 && (
    <table>
      <thead>
        <tr>
          <th>Nome do Aluno</th>
          <th>Nome da Matéria</th>
          <th>Total de Faltas</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      {relatorios.map((relatorio, index) => (
      <tr key={index}>
        <td>{relatorio.nomeAluno}</td>
        <td>{relatorio.nomeMateria}</td>
        <td>{relatorio.faltas}/5</td>
        {relatorio.faltas >= 2 && (
          <td>
            <button onClick={() => enviarEmail(relatorio.nomeAluno, relatorio.nomeMateria, relatorio.faltas)}>
              Enviar
            </button>
          </td>
        )}
      </tr>
    ))}
      </tbody>
    </table>
  )
}
    </div>
  );
};

export default List;
