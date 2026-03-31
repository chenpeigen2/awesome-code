import { MatrixProvider } from './contexts/MatrixContext';
import MatrixCanvas from './components/MatrixCanvas';
import ControlPanel from './components/ControlPanel';
import StatsOverlay from './components/StatsOverlay';
import TitleGlitch from './components/TitleGlitch';
import OverlayEffects from './components/OverlayEffects';

export default function App() {
  return (
    <MatrixProvider>
      <MatrixCanvas />
      <OverlayEffects />
      <TitleGlitch />
      <StatsOverlay />
      <ControlPanel />
    </MatrixProvider>
  );
}
