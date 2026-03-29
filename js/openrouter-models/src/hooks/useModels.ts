import { useState, useEffect } from "react";
import type { Model, ModelsResponse, ProviderGroup } from "../types/model";

const API_URL = "https://openrouter.ai/api/v1/models";

export function useModels() {
  const [models, setModels] = useState<Model[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch(API_URL)
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        return res.json() as Promise<ModelsResponse>;
      })
      .then((data) => {
        setModels(data.data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const providerGroups: ProviderGroup[] = (() => {
    const map = new Map<string, Model[]>();
    for (const model of models) {
      const provider = model.id.split("/")[0];
      if (!map.has(provider)) map.set(provider, []);
      map.get(provider)!.push(model);
    }
    return Array.from(map.entries())
      .map(([provider, models]) => ({
        provider,
        models: models.sort((a, b) => a.name.localeCompare(b.name)),
        count: models.length,
      }))
      .sort((a, b) => b.count - a.count);
  })();

  return { models, providerGroups, loading, error };
}
