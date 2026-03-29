export interface ModelPricing {
  prompt: string;
  completion: string;
  input_cache_read?: string;
}

export interface ModelArchitecture {
  modality: string;
  input_modalities: string[];
  output_modalities: string[];
  tokenizer: string;
  instruct_type: string | null;
}

export interface TopProvider {
  context_length: number;
  max_completion_tokens: number;
  is_moderated: boolean;
}

export interface Model {
  id: string;
  name: string;
  created: number;
  description: string;
  context_length: number;
  architecture: ModelArchitecture;
  pricing: ModelPricing;
  top_provider: TopProvider;
  supported_parameters: string[];
}

export interface ModelsResponse {
  data: Model[];
}

export interface ProviderGroup {
  provider: string;
  models: Model[];
  count: number;
}
