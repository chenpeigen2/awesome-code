import { useState, useMemo } from "react";
import { useModels } from "./hooks/useModels";
import { StatsBar } from "./components/StatsBar";
import { ProviderList } from "./components/ProviderList";
import { ModelTable } from "./components/ModelTable";
import { SearchBar } from "./components/SearchBar";
import "./App.css";

function App() {
  const { models, providerGroups, loading, error } = useModels();
  const [selectedProvider, setSelectedProvider] = useState<string | null>(null);
  const [search, setSearch] = useState("");

  const filteredModels = useMemo(() => {
    let result = models;
    if (selectedProvider) {
      result = result.filter((m) => m.id.startsWith(selectedProvider + "/"));
    }
    if (search) {
      const q = search.toLowerCase();
      result = result.filter(
        (m) =>
          m.name.toLowerCase().includes(q) ||
          m.id.toLowerCase().includes(q) ||
          m.description.toLowerCase().includes(q)
      );
    }
    return result;
  }, [models, selectedProvider, search]);

  if (loading) {
    return (
      <div className="app loading">
        <div className="loader" />
        <p>Loading models from OpenRouter API...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="app error">
        <h2>Error</h2>
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>
          <span className="logo-icon">&#9881;</span>
          OpenRouter Models Explorer
        </h1>
        <SearchBar value={search} onChange={setSearch} />
      </header>
      <StatsBar models={models} providers={providerGroups} />
      <div className="app-body">
        <ProviderList
          providers={providerGroups}
          selected={selectedProvider}
          onSelect={setSelectedProvider}
        />
        <main className="main-content">
          <div className="results-info">
            Showing {filteredModels.length} of {models.length} models
            {selectedProvider && (
              <button className="clear-filter" onClick={() => setSelectedProvider(null)}>
                Clear filter
              </button>
            )}
          </div>
          <ModelTable models={filteredModels} />
        </main>
      </div>
    </div>
  );
}

export default App;
