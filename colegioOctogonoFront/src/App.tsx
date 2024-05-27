import React from 'react';
import TurmaList from './Sistema';

const App: React.FC = () => {
  return (
    <div>
      <header><h2 className='hh2'>Sistema de Presença</h2></header>
      <div className="divturma"><TurmaList /></div>
    </div>
  );
};

export default App;
