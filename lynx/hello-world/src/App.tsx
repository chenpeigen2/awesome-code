import { useEffect } from '@lynx-js/react';
import './App.css';

export function App() {
  useEffect(() => {
    console.info('Hello, Lynx!');
  }, []);

  return (
    <page>
      <view className="container">
        <text className="title">Hello, World!</text>
        <text className="subtitle">Welcome to Lynx</text>
      </view>
    </page>
  );
}
